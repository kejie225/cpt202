// script.js
document.getElementById('delete-account-form').addEventListener('submit', function (e) {
    e.preventDefault();

    let valid = true;
    const password = document.getElementById('password').value;
    const verificationCode = document.getElementById('verification-code').value;
    const confirmationChecked = document.getElementById('confirmation-checkbox').checked;

    // Reset success and error messages
    document.getElementById('success-message').textContent = '';
    document.getElementById('pending-alert').style.display = 'none';

    // Validation
    if (!password || !verificationCode) {
        alert('Please enter both your password and verification code.');
        valid = false;
    }

    if (!confirmationChecked) {
        alert('You must confirm the account deletion.');
        valid = false;
    }

    if (valid) {
        // Simulate the process of checking for pending transactions
        const hasPendingTransactions = checkPendingTransactions(); // This is a mock function to simulate the check
        if (hasPendingTransactions) {
            displayPendingAlert(); // Show the alert if there are pending transactions
        } else {
            // Simulate sending a confirmation email and redirecting
            document.getElementById('success-message').textContent = 'Your account will be permanently deleted after processing (max 72 hours).';
            // Redirect after 72 hours (simulated)
            setTimeout(() => {
                window.location.href = '/home'; // Redirect to the homepage
            }, 3000); // 3 seconds delay for demo purposes
        }
    }
});

// Mock function to simulate checking for pending transactions
function checkPendingTransactions() {
    // Example: return true if there are pending transactions, false if none
    return false; // For this example, no pending transactions
}

// Display an alert when pending transactions exist
function displayPendingAlert() {
    document.getElementById('pending-alert').style.display = 'block';
    document.getElementById('pending-items').innerHTML = `
        <li><a href="/transactions">Unpaid orders</a></li>
        <li><a href="/subscriptions">Active services</a></li>
    `;
}