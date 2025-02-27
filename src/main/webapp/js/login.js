function handleFormSubmit(event) {
    event.preventDefault();

    const email = document.getElementById('email').value;
    const password1 = document.getElementById('password').value;

    const formData = {
        email: email,
        password: password
    };

    fetch('/sharing/main-servlet/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(formData)
    })
    .then(response => {
            if (response.ok) {

            } else {
                return response.json().then(data => {
                    document.getElementById('error-message').style.color = 'red';
                    document.getElementById('error-message').textContent = data.error;
                });
            }
        })
        .catch(error => {
            console.error('Error:', error);
            document.getElementById('error-message').style.color = 'red';
            document.getElementById('error-message').textContent = 'Wrong email or password';
        });

}
document.getElementById('registrationForm').addEventListener('submit', handleFormSubmit);