document.addEventListener("DOMContentLoaded", function () {
    // DOM Elements
    const addTaskBtn = document.getElementById("add-task-btn");
    const addTaskForm = document.getElementById("add-task-form");
    const taskListSection = document.getElementById("task-list-section");
    const sectionTitle = document.getElementById("section-title");
    const taskTableBody = document.getElementById("task-table-body"); // Body của bảng task
    const membersSection = document.getElementById("members-section");
    const profileSection = document.getElementById("profile-section");
    const tasksSection = document.getElementById("task-list-wrapper");
    const saveTaskBtn = document.getElementById("save-task-btn");
    const cancelTaskBtn = document.getElementById("cancel-task-btn");
    const staffModal = document.getElementById("staff-modal");
    const staffListContainer = document.getElementById("staff-list-container");
    const selectedStaffList = document.getElementById("selected-staff-list");
    const confirmStaffSelection = document.getElementById("confirm-staff-selection");
    const dashboardLink = document.getElementById("dashboard-link");
    const addStaffBtn = document.getElementById("add-staff-btn");

    let selectedStaff = [];
    const { workAreaId } = AppSession.getSessionData(); // Lấy workAreaId từ session

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
            taskListSection,
            tasksSection,
            addTaskForm,
            membersSection,
            profileSection,
        ];
        sections.forEach(section => {
            if (section) {
                section.style.display = "none";
            }
        });
    }

    // Dashboard function: Fetch and display tasks, and show only Dashboard sections
    function dashboard() {
        hideAllSections(); // Ẩn tất cả các phần khác

        // Hiển thị Dashboard
        sectionTitle.style.display = "block";
        sectionTitle.innerText = "Dashboard";
        taskListSection.style.display = "block";
        addTaskBtn.style.display = "block";

        // Fetch danh sách Task
        fetch(`/tasks/dashboard?workAreaId=${workAreaId}`)
            .then(response => response.ok ? response.json() : Promise.reject("Failed to fetch tasks"))
            .then(tasks => {
                taskTableBody.innerHTML = ""; // Xóa nội dung cũ trong bảng

                if (!tasks || tasks.length === 0) {
                    // Hiển thị nếu không có Task
                    taskTableBody.innerHTML = `
                        <tr>
                            <td colspan="7" class="text-center font-italic">Nothing to show</td>
                        </tr>`;
                    return;
                }

                // Hiển thị danh sách Task
                tasks.forEach(task => {
                    const memberAvatars = (task.assignedUsers || []).map(user => `
                        <img 
                            src="${user.avatarUrl || '/plugin/images/default_avatar.jpg'}" 
                            alt="${user.full_name}" 
                            class="avatar-tooltip rounded-circle" 
                            data-bs-toggle="tooltip" 
                            title="${user.full_name}" 
                            width="30" 
                            height="30" 
                            style="margin-right: 5px;"
                            onerror="this.src='/plugin/images/default_avatar.jpg';"
                        >`).join("");

                    const taskRow = `
                        <tr>
                            <td>${task.id}</td>
                            <td>${task.title}</td>
                            <td>${memberAvatars || "<span>No Members Assigned</span>"}</td>
                            <td>${task.createdAt || "N/A"}</td>
                            <td>${task.deadline || "N/A"}</td>
                            <td>${task.status || "Unknown"}</td>
                        </tr>`;
                    taskTableBody.innerHTML += taskRow;
                });

                // Khởi tạo tooltip
                initializeTooltips();
            })
            .catch(error => {
                console.error("Error fetching tasks:", error);
                taskTableBody.innerHTML = `
                    <tr>
                        <td colspan="7" class="text-center font-italic text-danger">An error occurred while loading tasks.</td>
                    </tr>`;
            });
    }

    // Initialize tooltips
    function initializeTooltips() {
        const tooltipTriggerList = Array.from(document.querySelectorAll(".avatar-tooltip"));
        tooltipTriggerList.forEach(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl));
    }

    // Show create a task form
    addTaskBtn?.addEventListener("click", function () {
        taskListSection.style.display = "none";
        addTaskBtn.style.display = "none";
        addTaskForm.style.display = "block";
        sectionTitle.innerText = "Create New Task";
    });

    // Cancel and return to dashboard
    cancelTaskBtn?.addEventListener("click", function () {
        addTaskForm.style.display = "none";
        taskListSection.style.display = "block";
        addTaskBtn.style.display = "block";
        sectionTitle.innerText = "Dashboard";
    });

    // Open modal to select staff
    addStaffBtn?.addEventListener("click", function () {
        if (!workAreaId) {
            Swal.fire({
                icon: "error",
                title: "Error",
                text: "No work area ID available to load staff.",
                confirmButtonColor: "#e74c3c",
            });
            return;
        }
        fetch(`/tasks/staff?workAreaId=${workAreaId}`)
            .then(response => response.json())
            .then(staffList => {
                staffListContainer.innerHTML = "";
                staffList.forEach(staff => {
                    const staffItem = document.createElement("div");
                    staffItem.classList.add("staff-item");
                    staffItem.innerHTML = `
                        <input type="checkbox" id="staff-${staff.id}" value="${staff.id}">
                        <label for="staff-${staff.id}">${staff.full_name}</label>
                    `;
                    staffListContainer.appendChild(staffItem);
                });
                $(staffModal).modal("show");
            })
            .catch(error => {
                Swal.fire({
                    icon: "error",
                    title: "Error",
                    text: "Failed to load staff list. Please try again.",
                    confirmButtonColor: "#e74c3c",
                });
            });
    });

    // Confirm selected staff
    confirmStaffSelection?.addEventListener("click", function () {
        selectedStaff = Array.from(staffListContainer.querySelectorAll("input[type='checkbox']:checked")).map(
            checkbox => ({
                id: parseInt(checkbox.value),
                name: checkbox.nextElementSibling.innerText,
            })
        );

        selectedStaffList.innerHTML = "";
        selectedStaff.forEach(staff => {
            const staffDisplay = document.createElement("div");
            staffDisplay.classList.add("selected-staff-item");
            staffDisplay.innerHTML = `
                <span>${staff.id} - ${staff.name}</span>
                <button class="deselect-staff-btn" data-id="${staff.id}">
                    <i class="fas fa-times"></i>
                </button>
            `;
            selectedStaffList.appendChild(staffDisplay);
        });

        // Add event listener to deselect staff
        selectedStaffList.querySelectorAll(".deselect-staff-btn").forEach(button => {
            button.addEventListener("click", function () {
                const idToRemove = parseInt(this.getAttribute("data-id"));
                selectedStaff = selectedStaff.filter(staff => staff.id !== idToRemove);
                this.parentElement.remove();
            });
        });

        $(staffModal).modal("hide");
    });

    // Save task
    saveTaskBtn?.addEventListener("click", function () {
        const title = document.getElementById("taskName").value;
        const description = document.getElementById("taskDescription").value;
        const deadline = document.getElementById("taskDeadline").value;
        const createdAt = document.getElementById("taskStartDate").value;
        const status = document.getElementById("taskStatus").value;

        if (!title || !description || !deadline || !createdAt) {
            Swal.fire({
                icon: "warning",
                title: "Incomplete Form",
                text: "Please fill in all required fields!",
                confirmButtonColor: "#5fad5f",
            });
            return;
        }

        if (new Date(deadline) < new Date(createdAt)) {
            Swal.fire({
                icon: "error",
                title: "Invalid Dates",
                text: "Deadline must be after start date.",
                confirmButtonColor: "#e74c3c",
            });
            return;
        }

        const taskData = {
            title,
            description,
            deadline,
            createdAt,
            status,
            assignedUsers: selectedStaff,
            workAreaId: workAreaId,
        };

        fetch("/tasks/create-task", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(taskData),
        })
            .then(response => (response.ok ? response.text() : Promise.reject(response.statusText)))
            .then(message => {
                Swal.fire({
                    icon: "success",
                    title: "Task Created!",
                    text: message,
                    confirmButtonColor: "#5fad5f",
                });
                addTaskForm.style.display = "none";
                taskListSection.style.display = "block";
                addTaskBtn.style.display = "block";
                sectionTitle.innerText = "Dashboard";
                selectedStaffList.innerHTML = "";
                dashboard(); // Refresh danh sách task
            })
            .catch(error => {
                Swal.fire({
                    icon: "error",
                    title: "Error",
                    text: "There was an error creating the task. Please try again.",
                    confirmButtonColor: "#e74c3c",
                });
            });
    });

    // Event Listener for Dashboard Link
    dashboardLink.addEventListener("click", dashboard);

    // Hiển thị Dashboard mặc định khi tải trang
    dashboard();
});
