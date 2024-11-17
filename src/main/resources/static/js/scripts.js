document.addEventListener("DOMContentLoaded", function () {
    // Select the sign-in button
    const signInButton = document.getElementById("signInButton");

    // Check if the button exists before adding an event listener
    if (signInButton) {
        signInButton.addEventListener("click", function (event) {
            event.preventDefault();

            // Retrieve values for email and password
            const email = document.getElementById("email")?.value.trim();
            const password = document.getElementById("password")?.value.trim();

            // Select error message elements
            const emailError = document.getElementById("emailError");
            const passwordError = document.getElementById("passwordError");
            const loginError = document.getElementById("loginError");

            // Reset error messages if elements exist
            if (emailError) emailError.style.display = "none";
            if (passwordError) passwordError.style.display = "none";
            if (loginError) loginError.style.display = "none";

            let isValid = true;

            // Validate email
            if (!email) {
                if (emailError) {
                    emailError.textContent = "Email is required.";
                    emailError.style.display = "block";
                }
                isValid = false;
            }

            // Validate password
            if (!password) {
                if (passwordError) {
                    passwordError.textContent = "Password is required.";
                    passwordError.style.display = "block";
                }
                isValid = false;
            }

            // Only proceed if inputs are valid
            if (isValid) {
                fetch("/login_validate", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({ email: email, password: password })
                })
                    .then(response => {
                        if (!response.ok) {
                            if (response.status === 401) {
                                // Display login error if authentication fails
                                if (loginError) {
                                    loginError.textContent = "Invalid email or password. Please try again.";
                                    loginError.style.display = "block";
                                }
                            } else {
                                if (loginError) {
                                    loginError.textContent = "An error occurred. Please try again.";
                                    loginError.style.display = "block";
                                }
                            }
                            return null;
                        }
                        return response.json();
                    })
                    .then(data => {
                        if (data) {
                            const { roleName, workAreaId } = data;

                            // Redirect based on user role
                            if (roleName === "Admin") {
                                window.location.href = "/admin/home";
                            } else {
                                window.location.href = "/api/home";
                            }
                        }
                    })
                    .catch(error => {
                        if (loginError) {
                            loginError.textContent = "An error occurred. Please try again.";
                            loginError.style.display = "block";
                        }
                        console.error("Error:", error);
                    });
            }
        });
    } else {
        console.error("SignInButton element not found in the DOM.");
    }
});
