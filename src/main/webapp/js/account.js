function loadUserData() {
    const token = localStorage.getItem('accessToken');
    fetch('/sharing/main-servlet/customer-info',{
        method: 'GET',
        headers: { 'Authorization': `Bearer ${token}` }
        })
        .then(response => {
            if (!response.ok) {
                window.location.href = "/sharing/login.html"
            }
            return response.json();
        })
        .then(data => {
            document.getElementById('name').textContent = 'Name: ' + data.name;
            document.getElementById('surname').textContent = 'Surname: ' + data.surname;
            document.getElementById('email').textContent = 'Email: ' + data.email;
        })
        .catch(error => {
            console.error('There was a problem with the fetch operation:', error);
        });
}
function handleLogout() {
  window.localStorage.clear();

  fetch('/sharing/main-servlet/logout', {
    method: 'POST',
    credentials: 'include',
  })
  .then(response => {
    if (response.ok) {
      window.location.replace('/sharing/login.html');
    } else {
      console.error('Logout failed');
    }
  })
  .catch(error => console.error('Error during logout:', error));
};

document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('logoutButton').addEventListener('click', handleLogout);
    loadUserData();
});

