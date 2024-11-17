document.addEventListener("DOMContentLoaded", function () {
    // Toggle sidebar
    const sidebarToggle = document.getElementById("sidebarToggle");
    const wrapper = document.getElementById("wrapper");

    sidebarToggle.addEventListener("click", function () {
        wrapper.classList.toggle("toggled");
    });

    // Quay lại Dashboard khi nhấn vào "Dashboard" trong sidebar
    document.getElementById("dashboard-link").addEventListener("click", function (event) {
        event.preventDefault();
        document.getElementById("dashboard-section").style.display = "block";
        document.getElementById("pending-users-section").style.display = "none";
        document.getElementById("section-title").innerText = "Dashboard";
    });

    // Sự kiện click cho phần Pending Requests
    document.getElementById("pending-requests-link").addEventListener("click", function (event) {
        event.preventDefault();

        fetch("/admin/pending-users")
            .then((response) => response.json())
            .then((data) => {
                const tableBody = document.getElementById("pending-users-table");
                tableBody.innerHTML = "";

                if (data.length === 0) {
                    const emptyRow = document.createElement("tr");
                    emptyRow.innerHTML = `
                        <td colspan="8" class="text-center text-muted" style="font-style: italic;">
                            Nothing to show
                        </td>
                    `;
                    tableBody.appendChild(emptyRow);
                } else {
                    data.forEach((user) => {
                        const row = document.createElement("tr");
                        row.innerHTML = `
                            <td>${user.id}</td>
                            <td>${user.full_name}</td>
                            <td>${user.email}</td>
                            <td>${user.phoneNumber}</td>
                            <td>${user.address}</td>
                            <td>${user.createdAt}</td>
                            <td>
                                <button class="btn btn-success btn-approve" data-user-id="${user.id}">Approve</button>
                                <button class="btn btn-danger btn-reject" data-user-id="${user.id}">Reject</button>
                            </td>
                        `;
                        tableBody.appendChild(row);
                    });
                }

                document.getElementById("section-title").innerText = "Pending User Requests";
                document.getElementById("dashboard-section").style.display = "none";
                document.getElementById("pending-users-section").style.display = "block";

                // Event listeners cho các nút Approve và Reject
                document.querySelectorAll(".btn-approve").forEach((button) => {
                    button.addEventListener("click", function () {
                        const userId = this.getAttribute("data-user-id");
                        fetch(`/admin/approve?userId=${userId}`, {
                            method: "PUT",
                        }).then((response) =>
                            response.ok ? alert("User approved!") : alert("Approval failed.")
                        );
                    });
                });

                document.querySelectorAll(".btn-reject").forEach((button) => {
                    button.addEventListener("click", function () {
                        const userId = this.getAttribute("data-user-id");
                        fetch(`/admin/delete?userId=${userId}`, {
                            method: "DELETE",
                        }).then((response) =>
                            response.ok ? alert("User rejected!") : alert("Rejection failed.")
                        );
                    });
                });
            })
            .catch((error) => console.error("Error loading pending users:", error));
    });
});
