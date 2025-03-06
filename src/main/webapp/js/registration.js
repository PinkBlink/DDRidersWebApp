function handleFormSubmit(event) {
    event.preventDefault();


    const name = document.getElementById('name').value;
    const surname = document.getElementById('surname').value;
    const email = document.getElementById('email').value;
    const password1 = document.getElementById('password1').value;
    const password2 = document.getElementById('password2').value;

    const formData = {
        name: name,
        surname: surname,
        email: email,
        password: password1
    };

    fetch('/sharing/main-servlet/registration', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(formData)
    })
    .then(response => {
            if (response.ok) {
                document.getElementById('error-message').style.color = 'green';
                document.getElementById('error-message').textContent = 'Registration successful! Redirecting to login...';
                window.location.href = '/sharing/login.html';
            } else {
                return response.json().then(data => {
                    document.getElementById('error-message').style.color = 'red';
                    document.getElementById('error-message').textContent = data.error || 'An unknown error occurred.';
                });
            }
        })
        .catch(error => {
            console.error('Error:', error);
            document.getElementById('error-message').style.color = 'red';
            document.getElementById('error-message').textContent = 'An error occurred during registration.';
        });

}
document.getElementById('registrationForm').addEventListener('submit', handleFormSubmit);