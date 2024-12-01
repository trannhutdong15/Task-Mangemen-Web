document.addEventListener("DOMContentLoaded", function () {
    // DOM Elements
    const sectionTitle = document.getElementById("section-title");
    const membersSection = document.getElementById("members-section");
    const membersTableBody = document.getElementById("members-table-body");
    const dashboardSection = document.getElementById("task-list-section");
    const tasksSection = document.getElementById("task-list-wrapper");
    const addTaskButton = document.getElementById("add-task-btn");
    const profileSection = document.getElementById("profile-section");
    const addTaskForm = document.getElementById("add-task-form");

    const membersLink = document.getElementById("members-link");

    const baseUrl = document.querySelector('meta[name="base-url"]').content;
    const usersEndpoint = "/api/users";

    // Get information from Session
    const { workAreaId } = AppSession.getSessionData();

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
            dashboardSection,
            tasksSection,
            addTaskButton,
            profileSection,
            membersSection,
            addTaskForm
        ];
        sections.forEach(section => {
            if (section) {
                section.style.display = "none";
            }
        });
    }

    // Fetch and show members in the Members section
    function showMembers() {
        hideAllSections(); // Ẩn tất cả các phần khác

        // Hiển thị tiêu đề và phần Members
        sectionTitle.style.display = "block";
        sectionTitle.innerText = "Members";
        membersSection.style.display = "block";

        // Fetch dữ liệu Members từ API
        fetch(`${usersEndpoint}?workAreaId=${workAreaId}`)
            .then(response => (response.ok ? response.json() : Promise.reject("Failed to fetch users")))
            .then(data => {
                membersTableBody.innerHTML = ""; // Clear existing table body

                if (data.length === 0) {
                    membersTableBody.innerHTML = `
                    <tr>
                        <td colspan="6" class="text-center font-italic">No members available</td>
                    </tr>`;
                } else {
                    // Populate members data
                    data.forEach(user => {
                        const avatarUrl = user.avatarUrl || `${baseUrl}default_avatar.jpg`;

                        // Hiển thị Task dưới dạng Badge với trạng thái
                        const taskBadges = (user.assignedTasks || []).length > 0
                            ? user.assignedTasks.map(task => {
                                const statusColor = getStatusColor(task.taskStatus);
                                const statusIcon = getStatusIcon(task.taskStatus);

                                return `
                                <span 
                                    class="badge task-badge" 
                                    style="background-color: ${statusColor}; color: white;" 
                                    data-bs-toggle="tooltip" 
                                    title="Name: ${task.taskName} | Status: ${task.taskStatus} | Deadline: ${task.taskDeadline || 'N/A'}"
                                >
                                    ${statusIcon} Task #${task.taskId || "Not Assigned"}
                                </span>`;
                            }).join("")
                            : `<span class="text-muted">No Task Assigned</span>`; // Nếu không có Task nào

                        membersTableBody.innerHTML += `
                        <tr>
                            <td>
                                <img 
                                    src="${avatarUrl}" 
                                    alt="Avatar" 
                                    class="rounded-circle member-avatar" 
                                    width="30" 
                                    height="30" 
                                    style="margin-right: 10px; vertical-align: middle;"
                                    onerror="this.src='${baseUrl}default_avatar.jpg';"
                                >
                                ${user.id}
                            </td>
                            <td>${user.full_name}</td>
                            <td>${user.email}</td>
                            <td>${user.phoneNumber || "N/A"}</td>
                            <td>${taskBadges}</td>
                        </tr>`;
                    });

                    // Initialize tooltips
                    const tooltipTriggerList = Array.from(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
                    tooltipTriggerList.forEach(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl));
                }
            })
            .catch(error => {
                console.error("Error fetching members:", error);
                membersTableBody.innerHTML = `
                <tr>
                    <td colspan="6" class="text-center font-italic text-danger">Failed to load members</td>
                </tr>`;
            });
    }

// Get background color based on task status
    function getStatusColor(status) {
        switch (status) {
            case "Not Started":
                return "#ffc107"; // Vàng
            case "In Progress":
                return "#6c757d"; // Xám
            case "Completed":
                return "#28a745"; // Xanh lá
            default:
                return "#007bff"; // Màu xanh dương mặc định
        }
    }

// Get icon based on task status
    function getStatusIcon(status) {
        switch (status) {
            case "Not Started":
                return "⏳";
            case "In Progress":
                return "⚙️";
            case "Completed":
                return "✅";
            default:
                return "❔";
        }
    }

    function getStatusClass(status) {
        switch (status) {
            case "Not Started":
                return "not-started";
            case "In Progress":
                return "in-progress";
            case "Completed":
                return "completed";
            default:
                return "text-muted"; // Mặc định cho trạng thái không xác định
        }
    }

    // Event Listener for Members Link
    membersLink.addEventListener("click", showMembers);

});
