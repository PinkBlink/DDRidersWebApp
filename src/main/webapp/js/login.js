function handleFormSubmit(event) {
    event.preventDefault();

    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

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
            const accessToken = response.headers.get('Authorization')?.replace('Bearer ', '');
                            if (accessToken) {
                                localStorage.setItem('accessToken', accessToken);
                            }
            window.location.href = "/sharing/account.html"
            } else {
                return response.json().then(data => {
                    document.getElementById('error-message').style.color = 'red';
                    document.getElementById('error-message').textContent = data.error;
                });
            }
        })
}
document.getElementById('loginForm').addEventListener('submit', handleFormSubmit);