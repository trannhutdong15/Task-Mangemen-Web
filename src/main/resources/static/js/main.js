
document.addEventListener("DOMContentLoaded", async () => {
    try {
        await AppSession.initializeSessionStorage(); // Khởi tạo session
        AppSession.validateSession(); // Kiểm tra tính hợp lệ
        console.log("Session storage is ready.");
    } catch (error) {
        console.error("Error during session initialization:", error);
    }
});

const AppSession = (function () {
    const API_ENDPOINTS = {
        userId: "/api/current-id",
        workAreaId: "/api/workAreaId",
        roleName: "/api/getRoleName",
    };

    // Lấy dữ liệu từ sessionStorage
    function getSessionData() {
        return {
            userId: sessionStorage.getItem("userId"),
            workAreaId: sessionStorage.getItem("workAreaId"),
            roleName: sessionStorage.getItem("roleName"),
        };
    }

    // Hàm gọi API để lấy dữ liệu
    async function fetchData(endpoint) {
        try {
            const response = await fetch(endpoint);
            if (!response.ok) {
                throw new Error(`Failed to fetch data from ${endpoint} with status ${response.status}`);
            }
            const data = await response.text();
            if (!data) {
                throw new Error(`No data returned from ${endpoint}`);
            }
            return data;
        } catch (error) {
            console.error("Error fetching data:", error);
            throw error; // Ném lỗi để xử lý ở nơi gọi
        }
    }

    // Khởi tạo dữ liệu vào sessionStorage
    async function initializeSessionStorage() {
        try {
            if (!sessionStorage.getItem("userId")) {
                const userId = await fetchData(API_ENDPOINTS.userId);
                sessionStorage.setItem("userId", userId);
                console.log("UserId initialized:", userId);
            }

            if (!sessionStorage.getItem("workAreaId")) {
                const workAreaId = await fetchData(API_ENDPOINTS.workAreaId);
                sessionStorage.setItem("workAreaId", workAreaId);
                console.log("WorkAreaId initialized:", workAreaId);
            }

            if (!sessionStorage.getItem("roleName")) {
                const roleName = await fetchData(API_ENDPOINTS.roleName);
                sessionStorage.setItem("roleName", roleName);
                console.log("RoleName initialized:", roleName);
            }

            console.log("Session storage initialized successfully:", getSessionData());
        } catch (error) {
            console.error("Failed to initialize session storage:", error);
            Swal.fire({
                icon: "error",
                title: "Session Initialization Failed",
                text: "Some required data could not be loaded. Please log in again.",
            }).then(() => {
                window.location.href = "/login"; // Chuyển hướng đến login nếu lỗi
            });
        }
    }

    // Hàm kiểm tra tính hợp lệ của sessionStorage
    function validateSession() {
        const { userId, workAreaId, roleName } = getSessionData();
        if (!userId || !workAreaId || !roleName) {
            console.warn("Session validation failed. Missing session data:", { userId, workAreaId, roleName });
            Swal.fire({
                icon: "warning",
                title: "Session Expired",
                text: "Your session is invalid or has expired. Please log in again.",
            }).then(() => {
                sessionStorage.clear(); // Xóa sạch session nếu không hợp lệ
                window.location.href = "/login"; // Chuyển hướng về login
            });
        } else {
            console.log("Session is valid:", { userId, workAreaId, roleName });
        }
    }

    return {
        initializeSessionStorage,
        getSessionData,
        validateSession,
    };
})();

