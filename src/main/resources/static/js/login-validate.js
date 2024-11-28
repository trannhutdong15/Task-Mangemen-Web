document.addEventListener("DOMContentLoaded", function () {
    const signInButton = document.getElementById("signInButton");

    if (!signInButton) {
        console.error("SignInButton element not found in the DOM.");
        return;
    }

    signInButton.addEventListener("click", async function (event) {
        event.preventDefault();

        const email = document.getElementById("email")?.value.trim();
        const password = document.getElementById("password")?.value.trim();

        let isValid = true;

        if (!email) {
            Swal.fire({
                icon: "warning",
                title: "Missing Email",
                text: "Please enter your email address.",
                confirmButtonColor: "#ff6f61",
            });
            isValid = false;
        }

        if (!password) {
            Swal.fire({
                icon: "warning",
                title: "Missing Password",
                text: "Please enter your password.",
                confirmButtonColor: "#ff6f61",
            });
            isValid = false;
        }

        if (!isValid) return;

        try {
            Swal.fire({
                title: "Logging in...",
                text: "Please wait while we validate your credentials.",
                allowOutsideClick: false,
                didOpen: () => Swal.showLoading(),
            });

            const response = await fetch("/login_validate", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ email, password }),
            });

            if (!response.ok) {
                const errorResponse = await response.json();

                switch (response.status) {
                    case 401:
                        if (errorResponse.error === "AccountNotApproved") {
                            Swal.fire({
                                icon: "info",
                                title: "Account Not Approved",
                                text: "Your account has not been approved yet. Please contact Admin.",
                                confirmButtonColor: "#ff6f61",
                            });
                        } else {
                            Swal.fire({
                                icon: "error",
                                title: "Invalid Login",
                                text: "Invalid email or password. Please try again.",
                                confirmButtonColor: "#ff6f61",
                            });
                        }
                        break;
                    case 404:
                        Swal.fire({
                            icon: "info",
                            title: "User Not Found",
                            text: "It seems this account does not exist. Please register first.",
                            confirmButtonColor: "#ff6f61",
                        });
                        break;
                    default:
                        Swal.fire({
                            icon: "error",
                            title: "Login Failed",
                            text: errorResponse.error || "An unexpected error occurred. Please try again.",
                            confirmButtonColor: "#ff6f61",
                        });
                        break;
                }
                return;
            }

            const data = await response.json();
            const { userId, workAreaId, roleName } = data;

            if (!userId || !workAreaId || !roleName) {
                throw new Error("Missing essential data from server response.");
            }

            // Lưu dữ liệu vào sessionStorage
            sessionStorage.setItem("userId", userId);
            sessionStorage.setItem("workAreaId", workAreaId);
            sessionStorage.setItem("roleName", roleName);

            Swal.fire({
                icon: "success",
                title: "Login Successful",
                text: "Redirecting to your dashboard...",
                showConfirmButton: false,
                timer: 1500,
            }).then(() => {
                if (roleName === "Admin") {
                    window.location.href = "/admin/home";
                } else {
                    window.location.href = "/api/home";
                }
            });
        } catch (error) {
            Swal.fire({
                icon: "error",
                title: "Unexpected Error",
                text: error.message || "An unexpected error occurred. Please try again.",
                confirmButtonColor: "#ff6f61",
            });
        }
    });
});
