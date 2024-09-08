// Hiển thị loading
function showLoading() {
    document.getElementById('loadingOverlay').style.display = 'flex'; // Hiển thị overlay loading
}

// Ẩn loading
function hideLoading() {
    document.getElementById('loadingOverlay').style.display = 'none'; // Ẩn overlay loading
}

// Ẩn loading sau khi trang đã tải hoàn toàn
window.addEventListener('load', function() {
    hideLoading(); // Ẩn loading khi trang đã tải xong
});

// Hiển thị loading khi click vào nút submit hoặc link chuyển trang
document.querySelectorAll('form, a').forEach(element => {
    if (element.tagName.toLowerCase() === 'form') {
        element.addEventListener('submit', function(event) {
            showLoading(); // Hiển thị loading khi submit form
        });
    } else if (element.tagName.toLowerCase() === 'a') {
        element.addEventListener('click', function(event) {
            if (element.getAttribute('href') !== '#' && !event.ctrlKey && !event.metaKey) {
                showLoading(); // Hiển thị loading khi click vào link chuyển trang
            }
        });
    }
});
