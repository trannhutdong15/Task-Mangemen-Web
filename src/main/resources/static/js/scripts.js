document.addEventListener("DOMContentLoaded", function() {
    const signInButton = document.getElementById("signInButton");

    signInButton.addEventListener("click", function(event) {
        event.preventDefault();

        const email = document.getElementById("email").value.trim();
        const password = document.getElementById("password").value.trim();

        const emailError = document.getElementById("emailError");
        const passwordError = document.getElementById("passwordError");
        const generalError = document.getElementById("generalError"); // Thêm phần tử để hiển thị lỗi chung

        emailError.style.display = "none";
        passwordError.style.display = "none";
        generalError.style.display = "none"; // Ẩn lỗi chung trước khi gửi yêu cầu

        let isValid = true;

        if (!email) {
            emailError.textContent = "Email is required.";
            emailError.style.display = "block";
            isValid = false;
        }

        if (!password) {
            passwordError.textContent = "Password is required.";
            passwordError.style.display = "block";
            isValid = false;
        }

        if (isValid) {
            fetch("/login_validate", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ email, password })
            })
                .then(response => {
                    if (response.ok) {
                        console.log("Login successful");
                        // Redirect or show success message here
                    } else if (response.status === 401) {
                        generalError.textContent = "Wrong email or password, try again";
                        generalError.style.display = "block";
                    } else {
                        console.error("Login failed with status:", response.status);
                    }
                })
                .catch(error => {
                    console.error("Error:", error);
                });
        }
    });
});
