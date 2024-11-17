document.addEventListener("DOMContentLoaded", function () {
    const addTaskBtn = document.getElementById("add-task-btn");
    const addTaskForm = document.getElementById("add-task-form");
    const taskListSection = document.getElementById("task-list-section");
    const sectionTitle = document.getElementById("section-title");
    const saveTaskBtn = document.getElementById("save-task-btn");
    const cancelTaskBtn = document.getElementById("cancel-task-btn");
    const addStaffBtn = document.getElementById("add-staff-btn");
    const staffModal = document.getElementById("staff-modal");
    const staffListContainer = document.getElementById("staff-list-container");
    const selectedStaffList = document.getElementById("selected-staff-list");
    const confirmStaffSelection = document.getElementById("confirm-staff-selection");
    const taskTableBody = document.getElementById("task-table-body");  // Body của bảng task

    let selectedStaff = [];
    let workAreaId = null;

    // Lấy ID khu vực làm việc
    fetch('/api/workAreaId')
        .then(response => response.ok ? response.text() : Promise.reject(response.statusText))
        .then(id => workAreaId = id)
        .catch(error => {
            console.error("Could not fetch workAreaId:", error);
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'Could not fetch work area ID. Please try again.',
                confirmButtonColor: '#e74c3c'
            });
        });

    // Lấy danh sách task và hiển thị
    function fetchTasks() {
        fetch("/tasks/dashboard")
            .then(response => response.ok ? response.json() : Promise.reject(response.statusText))
            .then(tasks => {
                taskTableBody.innerHTML = '';  // Xóa nội dung cũ trong bảng task
                if (tasks.length === 0) {
                    taskTableBody.innerHTML = `<tr><td colspan="7" class="text-center">Nothing to show</td></tr>`;
                } else {
                    tasks.forEach(task => {
                        const taskRow = `
                            <tr>
                                <td>${task.id}</td>
                                <td>${task.title}</td>
                                <td>${task.memberNames.join(", ")}</td>
                                <td>${task.createdAt}</td>
                                <td>${task.deadline}</td>
                                <td>${task.status}</td>
                            </tr>
                        `;
                        taskTableBody.innerHTML += taskRow;
                    });
                }
            })
            .catch(error => {
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: 'Failed to load tasks. Please try again.',
                    confirmButtonColor: '#e74c3c'
                });
                taskTableBody.innerHTML = `<tr><td colspan="7" class="text-center">An error occurred while loading tasks.</td></tr>`;
            });
    }

    // Hiển thị form tạo task
    addTaskBtn?.addEventListener("click", function () {
        taskListSection.style.display = "none";
        addTaskBtn.style.display = "none";
        addTaskForm.style.display = "block";
        sectionTitle.innerText = "Create New Task";
    });

    // Hủy và quay về dashboard
    cancelTaskBtn?.addEventListener("click", function () {
        addTaskForm.style.display = "none";
        taskListSection.style.display = "block";
        addTaskBtn.style.display = "block";
        sectionTitle.innerText = "Dashboard";
    });

    // Mở modal chọn staff
    addStaffBtn?.addEventListener("click", function () {
        if (!workAreaId) {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'No work area ID available to load staff.',
                confirmButtonColor: '#e74c3c'
            });
            return;
        }
        fetch(`/tasks/staff?workAreaId=${workAreaId}`)
            .then(response => response.json())
            .then(staffList => {
                staffListContainer.innerHTML = '';
                staffList.forEach(staff => {
                    const staffItem = document.createElement("div");
                    staffItem.classList.add("staff-item");
                    staffItem.innerHTML = `
                        <input type="checkbox" id="staff-${staff.id}" value="${staff.id}">
                        <label for="staff-${staff.id}">${staff.full_name}</label>
                    `;
                    staffListContainer.appendChild(staffItem);
                });
                $(staffModal).modal('show');
            })
            .catch(error => {
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: 'Failed to load staff list. Please try again.',
                    confirmButtonColor: '#e74c3c'
                });
            });
    });

    // Xác nhận lựa chọn staff
    confirmStaffSelection?.addEventListener("click", function () {
        selectedStaff = Array.from(staffListContainer.querySelectorAll("input[type='checkbox']:checked"))
            .map(checkbox => ({
                id: parseInt(checkbox.value),
                name: checkbox.nextElementSibling.innerText
            }));

        selectedStaffList.innerHTML = '';
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

        selectedStaffList.querySelectorAll(".deselect-staff-btn").forEach(button => {
            button.addEventListener("click", function () {
                const idToRemove = parseInt(this.getAttribute("data-id"));
                selectedStaff = selectedStaff.filter(staff => staff.id !== idToRemove);
                this.parentElement.remove();
            });
        });

        $(staffModal).modal('hide');
    });

    // Lưu task
    saveTaskBtn?.addEventListener("click", function () {
        const title = document.getElementById("taskName").value;
        const description = document.getElementById("taskDescription").value;
        const deadline = document.getElementById("taskDeadline").value;
        const createdAt = document.getElementById("taskStartDate").value;
        const status = document.getElementById("taskStatus").value;

        if (!title || !description || !deadline || !createdAt) {
            Swal.fire({
                icon: 'warning',
                title: 'Incomplete Form',
                text: 'Please fill in all required fields!',
                confirmButtonColor: '#5fad5f'
            });
            return;
        }

        if (new Date(deadline) < new Date(createdAt)) {
            Swal.fire({
                icon: 'error',
                title: 'Invalid Dates',
                text: 'Deadline must be after start date.',
                confirmButtonColor: '#e74c3c'
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
            workAreaId: workAreaId
        };

        fetch('/tasks/create-task', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(taskData),
        })
            .then(response => response.ok ? response.text() : Promise.reject(response.statusText))
            .then(message => {
                Swal.fire({
                    icon: 'success',
                    title: 'Task Created!',
                    text: message,
                    confirmButtonColor: '#5fad5f'
                });
                addTaskForm.style.display = "none";
                taskListSection.style.display = "block";
                addTaskBtn.style.display = "block";
                sectionTitle.innerText = "Dashboard";
                selectedStaffList.innerHTML = '';
                fetchTasks(); // Refresh danh sách task
            })
            .catch(error => {
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: 'There was an error creating the task. Please try again.',
                    confirmButtonColor: '#e74c3c'
                });
            });
    });

    // Tải danh sách task khi trang được tải
    fetchTasks();
});
