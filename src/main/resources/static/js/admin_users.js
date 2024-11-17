document.addEventListener("DOMContentLoaded", function () {
    const sidebarToggle = document.getElementById("sidebarToggle");
    const wrapper = document.getElementById("wrapper");

    sidebarToggle.addEventListener("click", function () {
        wrapper.classList.toggle("toggled");
    });

    // Xử lý khi bấm vào Users link để load danh sách người dùng
    document.getElementById("users-link").addEventListener("click", function (event) {
        event.preventDefault();

        fetch("/admin/users")
            .then((response) => response.json())
            .then((data) => {
                const tableBody = document.getElementById("users-table");
                tableBody.innerHTML = "";

                data.forEach((user) => {
                    const row = document.createElement("tr");
                    row.innerHTML = `
                        <td>${user.id}</td>
                        <td>${user.full_name}</td>
                        <td>${user.email}</td>
                        <td>${user.phoneNumber}</td>
                        <td>${user.address}</td>
                        <td>
                          <span class="role-text" data-user-id="${user.id}">${user.roleName || "Staff"}</span>
                          <select class="role-select d-none" data-user-id="${user.id}">
                              <option value="1" ${user.roleName === "Staff" ? "selected" : ""}>Staff</option>
                              <option value="2" ${user.roleName === "TeamLeader" ? "selected" : ""}>TeamLeader</option>
                              <option value="3" ${user.roleName === "Admin" ? "selected" : ""}>Admin</option>
                          </select>
                        </td>
                        <td>
                          <span class="workarea-text" data-user-id="${user.id}">${user.workAreaName || "Not assigned"}</span>
                          <select class="workarea-select d-none" data-user-id="${user.id}">
                              <option value="A01" ${user.workAreaName === "Assembly" ? "selected" : ""}>Assembly</option>
                              <option value="B01" ${user.workAreaName === "Painting" ? "selected" : ""}>Painting</option>
                              <option value="C01" ${user.workAreaName === "Metal Processing" ? "selected" : ""}>Metal Processing</option>
                              <option value="D01" ${user.workAreaName === "Warehouse" ? "selected" : ""}>Warehouse</option>
                              <option value="E01" ${user.workAreaName === "Inspection" ? "selected" : ""}>Inspection</option>
                          </select>
                        </td>
                        <td>
                            <button class="btn btn-secondary btn-edit" data-user-id="${user.id}">
                                <i class="fas fa-edit"></i>
                            </button>
                            <button class="btn btn-success btn-save d-none" data-user-id="${user.id}">
                                <i class="fas fa-save"></i>
                            </button>
                        </td>
                    `;
                    tableBody.appendChild(row);
                });

                document.getElementById("section-title").innerText = "Users";
                document.getElementById("dashboard-section").style.display = "none";
                document.getElementById("pending-users-section").style.display = "none";
                document.getElementById("users-section").style.display = "block";

                // Thêm sự kiện cho các nút Edit và Save như trước
                document.querySelectorAll(".btn-edit").forEach((button) => {
                    button.addEventListener("click", function () {
                        const userId = this.getAttribute("data-user-id");

                        this.classList.add("d-none");
                        const saveButton = document.querySelector(`.btn-save[data-user-id="${userId}"]`);
                        saveButton.classList.remove("d-none");

                        const roleSelect = document.querySelector(`.role-select[data-user-id="${userId}"]`);
                        const workareaSelect = document.querySelector(`.workarea-select[data-user-id="${userId}"]`);
                        const roleText = document.querySelector(`.role-text[data-user-id="${userId}"]`);
                        const workareaText = document.querySelector(`.workarea-text[data-user-id="${userId}"]`);

                        roleText.classList.add("d-none");
                        workareaText.classList.add("d-none");
                        roleSelect.classList.remove("d-none");
                        workareaSelect.classList.remove("d-none");
                    });
                });

                document.querySelectorAll(".btn-save").forEach((button) => {
                    button.addEventListener("click", function () {
                        const userId = this.getAttribute("data-user-id");
                        const roleId = document.querySelector(`.role-select[data-user-id="${userId}"]`).value;
                        const workAreaId = document.querySelector(`.workarea-select[data-user-id="${userId}"]`).value;

                        fetch(`/admin/assign-role?userId=${userId}&roleId=${roleId}`, {
                            method: "PUT",
                        })
                            .then(() =>
                                fetch(`/admin/assign-workarea?userId=${userId}&workAreaId=${workAreaId}&roleType=${roleId}`, {
                                    method: "PUT",
                                })
                            )
                            .then(() => {
                                alert("Role and Work Area updated!");

                                const roleText = document.querySelector(`.role-text[data-user-id="${userId}"]`);
                                const workareaText = document.querySelector(`.workarea-text[data-user-id="${userId}"]`);
                                const roleSelect = document.querySelector(`.role-select[data-user-id="${userId}"]`);
                                const workareaSelect = document.querySelector(`.workarea-select[data-user-id="${userId}"]`);
                                const editButton = document.querySelector(`.btn-edit[data-user-id="${userId}"]`);

                                roleText.innerText = roleSelect.options[roleSelect.selectedIndex].text;
                                workareaText.innerText = workareaSelect.options[workareaSelect.selectedIndex].text;

                                roleText.classList.remove("d-none");
                                workareaText.classList.remove("d-none");
                                roleSelect.classList.add("d-none");
                                workareaSelect.classList.add("d-none");

                                button.classList.add("d-none");
                                editButton.classList.remove("d-none");
                            })
                            .catch((error) => console.error("Error updating user:", error));
                    });
                });
            })
            .catch((error) => console.error("Error loading users:", error));
    });

    // Xử lý ẩn `users-section` khi chuyển sang trang `Pending Requests`
    document.getElementById("pending-requests-link").addEventListener("click", function (event) {
        event.preventDefault();
        document.getElementById("section-title").innerText = "Pending Requests";
        document.getElementById("dashboard-section").style.display = "none";
        document.getElementById("users-section").style.display = "none";
        document.getElementById("pending-users-section").style.display = "block";
    });
});