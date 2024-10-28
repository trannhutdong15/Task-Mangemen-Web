/* global Swal */
/** @type {import("sweetalert2")} */


document.addEventListener("DOMContentLoaded", function () {
    const registerForm = document.getElementById("registerForm");

    registerForm.addEventListener("submit", function (event) {
        event.preventDefault();
        let valid = true;

        // Clear previous error messages
        document.querySelectorAll(".error-message").forEach(msg => msg.remove());

        // Validate Full Name
        const fullName = document.getElementById("full_name");
        if (fullName.value.trim() === "") {
            showError(fullName, "Full name is required.");
            valid = false;
        }

        // Validate First Name
        const firstName = document.getElementById("first_name");
        if (firstName.value.trim() === "") {
            showError(firstName, "First name is required.");
            valid = false;
        }

        // Validate Last Name
        const lastName = document.getElementById("last_name");
        if (lastName.value.trim() === "") {
            showError(lastName, "Last name is required.");
            valid = false;
        }

        // Validate Email
        const email = document.getElementById("email");
        if (!isValidEmail(email.value.trim())) {
            showError(email, "Please enter a valid email address.");
            valid = false;
        }

        // Validate Password
        const password = document.getElementById("password");
        if (!isValidPassword(password.value.trim())) {
            showError(password, "Password must be over 8 characters, contain at least one uppercase letter, and one special character.");
            valid = false;
        }

        // Validate Phone Number
        const phoneNumber = document.getElementById("phone_number");
        if (!isValidPhoneNumber(phoneNumber.value.trim())) {
            showError(phoneNumber, "Please enter a valid phone number.");
            valid = false;
        }

        // Validate Address
        const address = document.getElementById("address");
        if (address.value.trim() === "") {
            showError(address, "Address is required.");
            valid = false;
        }

        if (valid) {
            const data = {
                full_name: fullName.value.trim(),
                first_name: firstName.value.trim(),
                last_name: lastName.value.trim(),
                email: email.value.trim(),
                password: password.value.trim(),
                phone_number: phoneNumber.value.trim(),
                address: address.value.trim()
            };

            fetch("/register_validate", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(data)
            })
                .then(response => {
                    if (response.ok) {
                        Swal.fire({
                            title: "Success!",
                        })
                        // Hiển thị thông báo thành công bằng SweetAlert2
                        Swal.fire({
                            icon: 'success',
                            title: 'Register Successfully!!!',
                            text: 'Wait 12 to 24 hours for your account to be active',
                            confirmButtonText: 'OK'
                        });
                        // Reset form nếu cần
                        registerForm.reset();
                    } else if (response.status === 409) {
                        return response.text().then(message => {
                            Swal.fire({
                            icon: 'error',
                            title: 'Registration Failed!',
                            text: message,
                            confirmButtonText: 'OK'
                            });
                        });
                    }
                })
                .catch(error => {
                    console.error("Error:", error);
                    Swal.fire({
                        icon: 'error',
                        title: 'An error occurred!',
                        text: 'Please try again later.',
                        confirmButtonText: 'OK'
                    });
                });
        }
    });

    // Helper function to show error messages
    function showError(input, message) {
        const error = document.createElement("div");
        error.className = "error-message";
        error.innerText = message;
        input.parentNode.appendChild(error);
        input.classList.add("is-invalid");

        input.addEventListener("input", function () {
            error.remove();
            input.classList.remove("is-invalid");
        });
    }

    // Helper function to validate email format
    function isValidEmail(email) {
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailPattern.test(email);
    }

    // Helper function to validate phone number format
    function isValidPhoneNumber(number) {
        const phonePattern = /^\d{10}$/; // Số điện thoại phải có 10 chữ số
        return phonePattern.test(number);
    }
    function isValidPassword(password) {
        const minLength = /.{8,}/;
        const hasUpperCase = /[A-Z]/;
        const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/;
        return minLength.test(password) && hasUpperCase.test(password) && hasSpecialChar.test(password);
    }
});
