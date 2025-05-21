document.addEventListener('DOMContentLoaded', function() {
    const signupForm = document.getElementById('signupForm');
    const passwordInput = document.getElementById('password');
    const confirmPasswordInput = document.getElementById('confirmPassword');
    const emailInput = document.getElementById('email');
    const termsCheckbox = document.getElementById('terms');
    const googleSignupBtn = document.getElementById('googleSignup');

    // 实时密码强度检查
    if (passwordInput) {
        passwordInput.addEventListener('input', function() {
            const strengthText = document.getElementById('password-strength');
            const strength = checkPasswordStrength(this.value);
            
            strengthText.textContent = strength.message;
            strengthText.style.color = strength.color;
        });
    }

    // 确认密码匹配检查
    if (confirmPasswordInput) {
        confirmPasswordInput.addEventListener('input', function() {
            const errorElement = document.getElementById('confirm-error');
            
            if (this.value !== passwordInput.value) {
                showError(errorElement, '密码不匹配');
            } else {
                hideError(errorElement);
            }
        });
    }

    // 邮箱格式验证
    if (emailInput) {
        emailInput.addEventListener('input', function() {
            const errorElement = document.getElementById('email-error');
            
            if (!validateEmail(this.value)) {
                showError(errorElement, '请输入有效的邮箱地址');
            } else {
                hideError(errorElement);
            }
        });
    }

    // 表单提交验证
    if (signupForm) {
        signupForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            let isValid = true;
            
            // 检查密码匹配
            if (passwordInput.value !== confirmPasswordInput.value) {
                showError(document.getElementById('confirm-error'), '密码不匹配');
                isValid = false;
            }
            
            // 检查邮箱格式
            if (!validateEmail(emailInput.value)) {
                showError(document.getElementById('email-error'), '请输入有效的邮箱地址');
                isValid = false;
            }
            
            // 检查条款同意
            if (!termsCheckbox.checked) {
                showError(document.getElementById('terms-error'), '请同意用户协议');
                isValid = false;
            }
            
            if (isValid) {
                // 这里可以添加AJAX提交或表单实际提交
                alert('表单验证通过，准备提交！');
                // 实际应用中取消下面这行的注释来提交表单
                // signupForm.submit();
            }
        });
    }

    // Google登录按钮
    if (googleSignupBtn) {
        googleSignupBtn.addEventListener('click', function() {
            alert('即将跳转到Google账号登录...');
            // 实际应用中这里会重定向到Google OAuth
            // window.location.href = '/auth/google';
        });
    }

    // 辅助函数
    function checkPasswordStrength(password) {
        if (password.length === 0) {
            return { message: '', color: 'transparent' };
        }
        if (password.length < 6) {
            return { message: '弱', color: '#ff4a4a' };
        }
        if (password.length < 10) {
            return { message: '中等', color: '#ffa500' };
        }
        if (!/[!@#$%^&*(),.?":{}|<>]/.test(password) || !/[A-Z]/.test(password)) {
            return { message: '强', color: '#4CAF50' };
        }
        return { message: '非常强', color: '#2e7d32' };
    }

    function validateEmail(email) {
        const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return re.test(email);
    }

    function showError(element, message) {
        if (element) {
            element.textContent = message;
            element.classList.add('show');
        }
    }

    function hideError(element) {
        if (element) {
            element.textContent = '';
            element.classList.remove('show');
        }
    }
});