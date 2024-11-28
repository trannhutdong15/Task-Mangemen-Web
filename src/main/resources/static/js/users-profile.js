document.addEventListener("DOMContentLoaded", function () {
    const sectionTitle = document.getElementById("section-title");
    const addTaskButton = document.getElementById("add-task-btn");
    const taskListSection = document.getElementById("task-list-section");
    const taskTableWrapper = document.getElementById("task-list-wrapper");
    const membersSection = document.getElementById("members-section");
    const profileSection = document.getElementById("profile-section");

    const profileLink = document.getElementById("profile-link");
    const logoutLink = document.getElementById("logout-link");
    const userNameElement = document.getElementById("user-name");
    const roleIconElement = document.getElementById("role-icon");

    const uploadOverlay = document.querySelector(".upload-overlay");
    const dashboardAvatarElement = document.getElementById("dashboard-avatar");

    const editAvatarElement = document.getElementById("edit-avatar");
    const avatarInputElement = document.getElementById("avatar");

    // Lấy thông tin từ session storage
    const { userId, roleName } = AppSession.getSessionData();
    let temporaryAvatarUrl = null;
    const STATIC_IMAGE_PATH = "/plugin/images/default_avatar.jpg";

    // Hiển thị icon role nếu là TeamLeader
    if (roleName === "TeamLeader" && roleIconElement && !roleIconElement.classList.contains("fas")) {
        roleIconElement.className = "fas fa-crown"; // Hiển thị icon vương miện
    }

    // Sự kiện chọn avatar để upload
    uploadOverlay.addEventListener("click", () => {
        avatarInputElement.click(); // Mở trình chọn file
    });

    // Upload ảnh tạm thời
    avatarInputElement.addEventListener("change", async function () {
        const file = avatarInputElement.files[0]; // Lấy file được chọn
        if (!file) return;

        Swal.fire({
            title: "Uploading...",
            text: "Please wait while the image is being uploaded.",
            allowOutsideClick: false,
            didOpen: () => {
                Swal.showLoading();
            },
        });

        const formData = new FormData();
        formData.append("file", file);

        try {
            const response = await fetch("/api/profile/avatar/temp", {
                method: "POST",
                body: formData,
            });

            if (!response.ok) throw new Error("Failed to upload image.");

            const data = await response.json();
            temporaryAvatarUrl = data.url;
            editAvatarElement.src = data.url;

            Swal.fire({
                icon: "success",
                title: "Upload Successful",
                text: "The image has been uploaded successfully.",
            });
        } catch (error) {
            console.error("Error uploading avatar:", error);
            Swal.fire({
                icon: "error",
                title: "Upload Failed",
                text: `Failed to upload image. ${error.message}`,
            });
        }
    });

    // Lấy thông tin người dùng từ API
    async function fetchUserProfile() {
        try {
            const response = await fetch(`/api/profile?userId=${userId}`);
            if (!response.ok) throw new Error("Failed to fetch profile data.");
            const data = await response.json();
            console.log("Fetched user profile:", data);

            // Hiển thị thông tin người dùng
            userNameElement.textContent = data.first_name;

            // Avatar dashboard
            if (data.avatarUrl && data.avatarUrl.startsWith("/plugin/images/")) {
                dashboardAvatarElement.src = data.avatarUrl;
            } else {
                dashboardAvatarElement.src = STATIC_IMAGE_PATH;
            }

            // Avatar chỉnh sửa
            editAvatarElement.src = data.avatarUrl || STATIC_IMAGE_PATH;

            // Avatar fallback
            editAvatarElement.onerror = () => {
                editAvatarElement.src = STATIC_IMAGE_PATH;
            };
            dashboardAvatarElement.onerror = () => {
                dashboardAvatarElement.src = STATIC_IMAGE_PATH;
            };

            // Update Profile Form
            document.getElementById("fullName").value = data.full_name;
            document.getElementById("email").value = data.email;
            document.getElementById("phoneNumber").value = data.phoneNumber;
            document.getElementById("address").value = data.address;
        } catch (error) {
            console.error("Error fetching user profile:", error);
            Swal.fire({
                icon: "error",
                title: "Error",
                text: "Failed to load user profile. Please try again.",
            });
        }
    }

    // Ẩn tất cả các phần
    function hideAllSections() {
        sectionTitle.style.display = "none";
        addTaskButton.style.display = "none";
        taskListSection.style.display = "none";
        taskTableWrapper.style.display = "none";
        membersSection.style.display = "none";
        profileSection.style.display = "none";
    }

    // Hiển thị Profile
    function showProfile() {
        hideAllSections();
        sectionTitle.style.display = "block";
        sectionTitle.innerText = "Profile";
        profileSection.style.display = "block";
        fetchUserProfile();
    }

    profileLink.addEventListener("click", function (e) {
        e.preventDefault();
        showProfile();
    });

    // Lưu thông tin Profile
    document.getElementById("save-profile-btn").addEventListener("click", function () {
        const profileData = {
            id: userId,
            full_name: document.getElementById("fullName").value,
            email: document.getElementById("email").value,
            phoneNumber: document.getElementById("phoneNumber").value,
            address: document.getElementById("address").value,
            avatarUrl: temporaryAvatarUrl || editAvatarElement.src,
        };

        fetch(`/api/profile/update?userId=${userId}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(profileData),
        })
            .then((response) => {
                if (!response.ok) throw new Error("Failed to update profile.");
                return response.json();
            })
            .then(() => {
                Swal.fire({
                    icon: "success",
                    title: "Profile Updated",
                    text: "Your profile has been updated successfully!",
                });
                dashboardAvatarElement.src = profileData.avatarUrl;
                showProfile();
            })
            .catch((error) => {
                console.error("Error updating profile:", error);
                Swal.fire({
                    icon: "error",
                    title: "Error",
                    text: "Failed to update profile. Please try again.",
                });
            });
    });

    // Xử lý logout
    logoutLink.addEventListener("click", function (e) {
        e.preventDefault();
        fetch("/api/logout", {
            method: "POST",
        })
            .then((response) => {
                if (!response.ok) throw new Error("Failed to logout.");
                Swal.fire({
                    icon: "success",
                    title: "Logged Out",
                    text: "You have been logged out successfully!",
                }).then(() => {
                    window.location.href = "/login";
                });
            })
            .catch((error) => {
                console.error("Error during logout:", error);
                Swal.fire({
                    icon: "error",
                    title: "Error",
                    text: "Failed to logout. Please try again.",
                });
            });
    });

    // Khi trang home tải, tự động hiển thị Dashboard và avatar
    fetchUserProfile();
});
