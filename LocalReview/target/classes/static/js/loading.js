// Hiển thị loading
function showLoading() {
    document.getElementById('loadingOverlay').style.display = 'flex'; // Hiển thị overlay loading
}

// Ẩn loading
function hideLoading() {
    document.getElementById('loadingOverlay').style.display = 'none'; // Ẩn overlay loading
}

// Ẩn loading sau khi trang đã tải hoàn toàn hoặc khi trang được hiển thị lại từ cache
window.addEventListener('load', hideLoading);
window.addEventListener('pageshow', function(event) {
    // Kiểm tra nếu trang được load lại từ cache (persisted === true)
    if (event.persisted) {
        hideLoading(); // Ẩn loading khi trang hiển thị lại từ cache
    }
});

// Hiển thị loading khi người dùng bắt đầu chuyển sang trang khác
window.addEventListener('beforeunload', function() {
    showLoading(); // Hiển thị loading khi trang bắt đầu chuyển
});

// Hiển thị loading khi click vào nút submit hoặc link chuyển trang
document.querySelectorAll('form, a').forEach(element => {
    if (element.tagName.toLowerCase() === 'form') {
        element.addEventListener('submit', function() {
            showLoading(); // Hiển thị loading khi submit form
        });
    } else if (element.tagName.toLowerCase() === 'a') {
        element.addEventListener('click', function(event) {
            const href = element.getAttribute('href');
            // Kiểm tra nếu không phải là link trống và không giữ phím ctrl hoặc meta (để mở tab mới)
            if (href && href !== '#' && !event.ctrlKey && !event.metaKey) {
                event.preventDefault(); // Ngăn chặn hành vi mặc định
                showLoading(); // Hiển thị loading trước khi chuyển hướng
                setTimeout(() => {
                    window.location.href = href; // Chuyển hướng thủ công
                }, 1000); // Delay nhỏ để đảm bảo loading hiển thị
            }
        });
    }
});
