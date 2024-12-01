document.addEventListener("DOMContentLoaded", function () {
    // Toggle sidebar
    const sidebarToggle = document.getElementById("sidebarToggle");
    const wrapper = document.getElementById("wrapper");

    sidebarToggle.addEventListener("click", function () {
        wrapper.classList.toggle("toggled");
    });

    // Fetch và hiển thị tasks khi trang tải
    loadTasks();

    // Hàm load các tasks
    function loadTasks() {
        fetch("/admin/getTasks")
            .then((response) => response.json())
            .then((tasks) => {
                const tasksContainer = document.getElementById("tasks-container");
                tasksContainer.innerHTML = "";  // Xóa các task cũ nếu có

                if (tasks.length === 0) {
                    const emptyMessage = document.createElement("p");
                    emptyMessage.innerText = "No tasks available.";
                    tasksContainer.appendChild(emptyMessage);
                } else {
                    tasks.forEach((task) => {
                        const taskRow = createTaskRow(task);
                        tasksContainer.appendChild(taskRow);
                    });
                }

                // Initialize tooltips after adding tasks
                initializeTooltips();
            })
            .catch((error) => console.error("Error loading tasks:", error));
    }

    // Hàm tạo một task row
    function createTaskRow(task) {
        const taskRow = document.createElement("div");
        taskRow.classList.add("row", "task-row", "mb-3", "p-3", "border", "rounded", "shadow-sm");

        taskRow.innerHTML = `
            <div class="col-3">
                <p class="task-title">${task.title}</p>
            </div>
            <div class="col-2">
                <div class="task-date">
                    <i class="fas fa-calendar-day"></i> ${formatDate(task.createdAt)}
                </div> <!-- Display formatted start date -->
            </div>
            <div class="col-2">
                <div class="members-container">
                    ${task.assignedUsers.map(user => `
                        <img src="${user.avatarUrl}" alt="${user.full_name}" class="avatar avatar-tooltip" data-bs-toggle="tooltip" data-bs-placement="top" title="${user.full_name}">
                    `).join('')}
                </div>
            </div>
            <div class="col-2">
                <div class="task-date">
                    <i class="fas fa-calendar-alt"></i> ${formatDate(task.deadline)}
                </div> <!-- Display formatted deadline -->
            </div>
            <div class="col-2">
                <p class="task-status">${task.status}</p>
            </div>
            <div class="col-1">
                <span class="work-area" data-bs-toggle="tooltip" data-bs-placement="top" title="ID: ${task.workAreaId}">
                    <span class="work-area-box">${task.workAreaName}</span>
                </span>
            </div>
        `;

        return taskRow;
    }

    document.getElementById("dashboard-link").addEventListener("click", function (event) {
        event.preventDefault();
        document.getElementById("section-title").innerText = "Dashboard";
        document.getElementById("dashboard-section").style.display = "block";
        document.getElementById("users-section").style.display = "none";
        document.getElementById("pending-users-section").style.display = "none";
    });

    function initializeTooltips() {
        // Khởi tạo tooltips cho các phần tử có thuộc tính `data-bs-toggle="tooltip"`
        const tooltipTriggerList = Array.from(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
        tooltipTriggerList.forEach((tooltipTriggerEl) => {
            new bootstrap.Tooltip(tooltipTriggerEl); // Khởi tạo Tooltip cho mỗi phần tử
        });
    }


    function formatDate(dateString) {
        const date = new Date(dateString);
        return date.toLocaleDateString("en-US");  // Tuỳ chỉnh định dạng ngày nếu cần
    }
});
