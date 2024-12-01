document.addEventListener("DOMContentLoaded", function () {
    // DOM Elements
    const sectionTitle = document.getElementById("section-title");
    const tasksSection = document.getElementById("task-list-wrapper");
    const taskTableBody = document.getElementById("task-table-body-wrapper");
    const dashboardSection = document.getElementById("task-list-section");
    const addTaskButton = document.getElementById("add-task-btn");
    const membersSection = document.getElementById("members-section");
    const profileSection = document.getElementById("profile-section");
    const addTaskForm = document.getElementById("add-task-form");

    const tasksLink = document.getElementById("tasks-link");

    const baseUrl = document.querySelector('meta[name="base-url"]').content;
    const tasksEndpoint = "/tasks/dashboard";

    const workAreaId = sessionStorage.getItem("workAreaId");
    const roleName = sessionStorage.getItem("roleName");



    const updateTaskModal = document.getElementById("updateTaskModal");
    const updateTaskName = document.getElementById("update-taskName");
    const updateTaskDescription = document.getElementById("update-taskDescription");
    const updateTaskDeadline = document.getElementById("update-taskDeadline");
    const updateTaskStartDate = document.getElementById("update-taskStartDate");
    const updateTaskStatus = document.getElementById("update-taskStatus");
    const updateSelectedMembers = document.getElementById("update-selectedMembers");
    const updateManageMembersBtn = document.getElementById("update-manageMembersBtn");
    const updateSaveTaskBtn = document.getElementById("update-saveTaskBtn");
    const manageMembersModal = document.getElementById("manageMembersModal");
    const updateMembersListContainer = document.getElementById("update-membersListContainer");
    const updateConfirmMembersBtn = document.getElementById("update-confirmMembersBtn");
    let currentTaskId = null;
    let selectedMembers = [];
    const cancelUpdateTaskBtn = updateTaskModal.querySelector(".btn-close"); // Nút "X" trong modal Update Task
    const cancelManageMembersBtn = manageMembersModal.querySelector(".btn-close");

    // Base URL cho các endpoint của bạn
    const baseApiUrl = '/tasks';



// Các URL tương ứng với controller
    const fetchStaffUrl = `${baseApiUrl}/staff`;
    const fetchTaskDetailsUrl = (taskId) => `${baseApiUrl}/details/${taskId}`;
    const updateTaskUrl = (taskId) => `${baseApiUrl}/update/${taskId}`;

    // Validate session data
    if (!workAreaId) {
        console.error("Missing required session data: workAreaId!");
        Swal.fire({
            icon: "error",
            title: "Session Error",
            text: "Session data is missing. Please log in again.",
            confirmButtonColor: "#e74c3c",
        }).then(() => {
            window.location.href = "/login";
        });
        return;
    }

    // Hide all sections
    function hideAllSections() {
        const sections = [
            sectionTitle,
            tasksSection,
            dashboardSection,
            addTaskButton,
            membersSection,
            profileSection,
            addTaskForm
        ];
        sections.forEach(section => {
            if (section) {
                section.style.display = "none";
            }
        });
    }

    // Fetch and show tasks in the Tasks section
     function showTasks() {
        hideAllSections();


        tasksSection.style.display = "block";
        sectionTitle.style.display = "block";
        sectionTitle.innerText = "Tasks";


        fetch(`${tasksEndpoint}?workAreaId=${workAreaId}`)
            .then(response => (response.ok ? response.json() : Promise.reject("Failed to fetch tasks")))
            .then(data => {
                taskTableBody.innerHTML = ""; // Xóa dữ liệu cũ trong bảng

                if (data.length === 0) {

                    taskTableBody.innerHTML = `
                        <tr>
                            <td colspan="8" class="text-center font-italic">No tasks available</td>
                        </tr>`;
                } else {

                    data.forEach(task => {
                        // Avatar của các thành viên trong Task
                        const memberAvatars = (task.assignedUsers || []).map(user => `
                            <img 
                                src="${user.avatarUrl || `${baseUrl}default_avatar.jpg`}" 
                                alt="${user.full_name}" 
                                class="avatar-tooltip rounded-circle" 
                                data-bs-toggle="tooltip" 
                                title="${user.full_name}" 
                                width="30" 
                                height="30" 
                                data-user-id="${user.id}" 
                                onerror="this.src='${baseUrl}default_avatar.jpg';"
                            >`).join("");

                        // Thêm hàng vào bảng
                        taskTableBody.innerHTML += `
                        <tr data-task-id="${task.id}" data-assigned-users='${JSON.stringify(task.assignedUsers || [])}'>
                                <td>${task.id}</td>
                                <td>${task.title}</td>
                                <td>${task.description || "No Description"}</td>
                                <td>${memberAvatars || "<span>No Members Assigned</span>"}</td>
                                <td>${task.createdAt || "N/A"}</td>
                                <td>${task.deadline || "N/A"}</td>
                                <td>${task.status || "Unknown"}</td>
                                <td>
                                    ${roleName === 'TeamLeader' ? `
                                        <button class="btn btn-sm btn-primary edit-task-btn">Edit</button>
                                        <button class="btn btn-sm btn-danger delete-task-btn">Delete</button>
                                                                                ` : `
                                        <button class="btn btn-sm btn-success submit-task-btn">Submit</button>
                                                        `}
                                </td>
                            </tr>`;
                    });

                    // Khởi tạo tooltips
                    initializeTooltips();
                }
            })
            .catch(error => {
                console.error("Error fetching tasks:", error);
                taskTableBody.innerHTML = `
                    <tr>
                        <td colspan="8" class="text-center font-italic text-danger">Failed to load tasks</td>
                    </tr>`;
            });
    }

    // Initialize tooltips
    function initializeTooltips() {
        const tooltipTriggerList = Array.from(document.querySelectorAll('.avatar-tooltip'));
        tooltipTriggerList.forEach(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl));
    }

    // Event Listener for Tasks Link
    tasksLink.addEventListener("click", showTasks);

    // Helper function to render selected members
    function renderSelectedMembers() {
        updateSelectedMembers.innerHTML = selectedMembers
            .map(member => `
            <div class="selected-member" data-user-id="${member.id}">
                <img src="${member.avatarUrl || `${baseUrl}default_avatar.jpg`}" alt="${member.full_name}" class="rounded-circle" width="40" height="40" />
                <span>${member.full_name}</span>
                <button class="btn btn-sm btn-danger ms-2 remove-member-btn" data-user-id="${member.id}">
                    <i class="fas fa-times"></i>
                </button>
            </div>
        `).join("");

        document.querySelectorAll(".remove-member-btn").forEach(button => {
            button.addEventListener("click", function () {
                const userId = parseInt(this.dataset.userId);
                selectedMembers = selectedMembers.filter(member => member.id !== userId);
                renderSelectedMembers();
            });
        });
    }

// Fetch task details and populate modal
    async function fetchTaskDetails(taskId) {
        try {
            const response = await fetch(fetchTaskDetailsUrl(taskId));
            if (!response.ok) throw new Error("Failed to fetch task details");
            const task = await response.json();

            // Populate modal fields
            updateTaskName.value = task.title || "";
            updateTaskDescription.value = task.description || "";
            updateTaskDeadline.value = task.deadline || "";
            updateTaskStartDate.value = task.createdAt || "";
            updateTaskStatus.value = task.status || "Not Started";

            currentTaskId = task.id;
            selectedMembers = task.assignedUsers || [];
            renderSelectedMembers();

            // Open the modal
            const modalInstance = new bootstrap.Modal(updateTaskModal);
            modalInstance.show();
        } catch (error) {
            console.error("Error fetching task details:", error);
            await Swal.fire({
                icon: "error",
                title: "Error",
                text: "Failed to load task details. Please try again.",
            });
        }
    }

// Fetch available members for selection
    async function fetchAvailableMembers() {
        try {
            const workAreaId = sessionStorage.getItem("workAreaId");
            const response = await fetch(`${fetchStaffUrl}?workAreaId=${encodeURIComponent(workAreaId)}`);
            if (!response.ok) throw new Error("Failed to fetch members");
            const members = await response.json();

            // Populate the member list
            updateMembersListContainer.innerHTML = members
                .map(member => `
                <div class="list-group-item">
                    <input type="checkbox" id="member-${member.id}" value="${member.id}" ${selectedMembers.some(m => m.id === member.id) ? "checked" : ""}>
                    <label for="member-${member.id}">
                        <img src="${member.avatarUrl || `${baseUrl}default_avatar.jpg`}" alt="${member.full_name}" class="rounded-circle" width="30" height="30" />
                        ${member.full_name}
                    </label>
                </div>
            `).join("");
        } catch (error) {
            console.error("Error fetching members:", error);
            await Swal.fire({
                icon: "error",
                title: "Error",
                text: "Failed to load available members. Please try again.",
            });
        }
    }

// Event: Manage member button click
    updateManageMembersBtn.addEventListener("click", async () => {
        await fetchAvailableMembers();
        const modalInstance = new bootstrap.Modal(manageMembersModal);
        modalInstance.show();
    });

// Event: Confirm member selection
    updateConfirmMembersBtn.addEventListener("click", () => {
        const selectedCheckBoxes = Array.from(updateMembersListContainer.querySelectorAll("input[type='checkbox']:checked"));
        const newSelectedMembers = selectedCheckBoxes.map(box => {
            const id = parseInt(box.value);
            const label = box.nextElementSibling;
            return {
                id,
                full_name: label.textContent.trim(),
                avatarUrl: label.querySelector("img")?.src || `${baseUrl}default_avatar.jpg`,
            };
        });

        if (newSelectedMembers.length > 3) {
            Swal.fire({
                icon: "warning",
                title: "Limit Exceeded",
                text: "You can assign up to 3 members only.",
            });
            return;
        }

        selectedMembers = newSelectedMembers;
        renderSelectedMembers();
        bootstrap.Modal.getInstance(manageMembersModal).hide();
    });

// Event: Save task updates
    updateSaveTaskBtn.addEventListener("click", async () => {
        try {
            const updatedTask = {
                id: currentTaskId,
                title: updateTaskName.value,
                description: updateTaskDescription.value,
                deadline: updateTaskDeadline.value,
                createdAt: updateTaskStartDate.value,
                status: updateTaskStatus.value,
                assignedUsers: selectedMembers.map(member => ({ id: member.id })),
            };

            const response = await fetch(updateTaskUrl(currentTaskId), {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(updatedTask),
            });

            if (!response.ok) throw new Error("Failed to update task");

            await Swal.fire({
                icon: "success",
                title: "Task Updated",
                text: "The task has been successfully updated.",
            });

            // Refresh task list
            showTasks(); // Ensure showTasks is globally available
            bootstrap.Modal.getInstance(updateTaskModal).hide();
        } catch (error) {
            console.error("Error updating task:", error);
            await Swal.fire({
                icon: "error",
                title: "Error",
                text: "Failed to update task. Please try again.",
            });
        }
    });

// Handle cancel button in Update Task modal
    cancelUpdateTaskBtn.addEventListener("click", () => {
        bootstrap.Modal.getInstance(updateTaskModal).hide();
    });

// Handle cancel button in Manage Members modal
    cancelManageMembersBtn.addEventListener("click", () => {
        bootstrap.Modal.getInstance(manageMembersModal).hide();
    });

// Event: Edit task button click
    document.addEventListener("click", event => {
        if (event.target.classList.contains("edit-task-btn")) {
            const taskRow = event.target.closest("tr");
            const taskId = parseInt(taskRow.dataset.taskId);
            fetchTaskDetails(taskId);
        }
    });

    // Gắn sự kiện click vào nút xóa task trong danh sách
    document.addEventListener('click', (event) => {
        if (event.target.classList.contains('delete-task-btn')) {
            const taskId = parseInt(event.target.closest('tr').dataset.taskId);

            // Confirm xóa task
            Swal.fire({
                title: 'Are you sure?',
                text: "You won't be able to revert this action!",
                icon: 'warning',
                showCancelButton: true,
                confirmButtonText: 'Yes, delete it!',
                cancelButtonText: 'Cancel',
            }).then((result) => {
                if (result.isConfirmed) {
                    deleteTask(taskId);  // Gọi hàm deleteTask với taskId
                }
            });
        }
    });

    async function deleteTask(taskId) {
        try {
            // Gửi request DELETE đến server
            const response = await fetch(`/tasks/delete/${taskId}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                },
            });

            // Kiểm tra nếu response không thành công
            if (!response.ok) {
                throw new Error('Failed to delete task');
            }

            // Nếu xóa thành công, hiển thị thông báo
            await Swal.fire({
                icon: 'success',
                title: 'Task Deleted',
                text: 'The task has been successfully deleted.',
            });

            // Refresh danh sách các tasks sau khi xóa (gọi lại hàm showTasks hoặc tương tự)
            showTasks();  // Giả sử bạn có hàm này để load lại task list

        } catch (error) {
            console.error('Error deleting task:', error);

            // Hiển thị thông báo lỗi nếu có vấn đề xảy ra
            await Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'Failed to delete task. Please try again.',
            });
        }
    }




});
