document.addEventListener("DOMContentLoaded", function () {
    const membersLink = document.getElementById("members-link"); // Link để mở Members
    const dashboardLink = document.getElementById("dashboard-link"); // Link để quay lại Dashboard
    const sectionTitle = document.getElementById("section-title"); // Tiêu đề của phần nội dung
    const taskListSection = document.getElementById("task-list-section"); // Phần Dashboard (Task List)
    const addTaskBtn = document.getElementById("add-task-btn"); // Nút Add Task ở Dashboard
    const membersSection = document.getElementById("members-section"); // Phần Members
    const membersTableBody = document.getElementById("members-table-body"); // Body của bảng Members

    let workAreaId = null;

    // Lấy ID khu vực làm việc
    fetch('/api/workAreaId')
        .then(response => response.ok ? response.text() : Promise.reject(response.statusText))
        .then(id => {
            workAreaId = id;
            console.log("Fetched WorkAreaId:", workAreaId);
        })
        .catch(error => {
            console.error("Could not fetch workAreaId:", error);
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: 'Could not fetch work area ID. Please try again.',
                confirmButtonColor: '#e74c3c'
            });
        });

    // Hàm hiển thị danh sách Members
    function displayMembers() {
        if (!workAreaId) {
            Swal.fire({
                icon: 'warning',
                title: 'Work Area ID Missing',
                text: 'Please try again later.',
                confirmButtonColor: '#f1c40f'
            });
            return;
        }

        fetch(`/api/users?workAreaId=${workAreaId}`)
            .then(response => response.ok ? response.json() : Promise.reject(response.statusText))
            .then(users => {
                console.log("User data:", users); // Kiểm tra cấu trúc dữ liệu từ API
                membersTableBody.innerHTML = ''; // Xóa nội dung cũ trong bảng Members
                if (users.length === 0) {
                    membersTableBody.innerHTML = `<tr><td colspan="6" class="text-center">Nothing to show</td></tr>`;
                } else {
                    users.forEach(user => {
                        const taskId = user.assignedTasks && user.assignedTasks.length > 0 && user.assignedTasks[0].taskId ? user.assignedTasks[0].taskId : "Not Assigned";
                        const taskName = user.assignedTasks && user.assignedTasks.length > 0 && user.assignedTasks[0].taskName ? user.assignedTasks[0].taskName : "Not Assigned";

                        const memberRow = `
                            <tr>
                                <td>${user.id}</td>
                                <td>${user.full_name}</td>
                                <td>${user.email}</td>
                                <td>${user.phoneNumber}</td>
                                <td>${taskId}</td>
                                <td>${taskName}</td>
                            </tr>
                        `;
                        membersTableBody.innerHTML += memberRow;
                    });
                }
            })
            .catch(error => {
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: 'Failed to load members. Please try again.',
                    confirmButtonColor: '#e74c3c'
                });
                membersTableBody.innerHTML = `<tr><td colspan="6" class="text-center">An error occurred while loading members.</td></tr>`;
            });
    }

    // Hiển thị phần Members khi nhấn vào link "Members"
    membersLink?.addEventListener("click", function () {
        console.log("Members link clicked"); // Log kiểm tra sự kiện click
        sectionTitle.innerText = "Members"; // Đổi tiêu đề
        taskListSection.style.display = "none"; // Ẩn Dashboard
        addTaskBtn.style.display = "none"; // Ẩn nút thêm task
        membersSection.style.display = "block"; // Hiển thị bảng Members
        displayMembers(); // Gọi hàm hiển thị dữ liệu
    });

    // Trở về Dashboard khi nhấn link "Dashboard"
    dashboardLink?.addEventListener("click", function () {
        console.log("Dashboard link clicked"); // Log kiểm tra sự kiện click
        sectionTitle.innerText = "Dashboard"; // Đổi tiêu đề
        membersSection.style.display = "none"; // Ẩn bảng Members
        taskListSection.style.display = "block"; // Hiển thị Dashboard
        addTaskBtn.style.display = "block"; // Hiển thị nút thêm task
    });
});
