// Hàm để lấy store ID từ URL hiện tại
function getStoreId() {
    var url = window.location.href;
    var matches = url.match(/\/store\/detail\/([a-zA-Z0-9-]+)$/);
    return matches ? matches[1] : null;
}

// Hàm để xử lý việc gửi form đánh giá
function submitReviewForm() {
    var storeId = getStoreId();
    if (!storeId) {
        alert('Store ID đang bị thiếu!');
        return;
    }

    var formData = new FormData();
    formData.append('comment', document.getElementById('comment').value);
    
    // Lấy giá trị đánh giá từ input ẩn
    var ratingValue = document.getElementById('ratingValue').value;
    if (ratingValue === "0" || ratingValue === "undefined") {
        alert('Vui lòng chọn một đánh giá hợp lệ từ 1 đến 5.');
        return;
    }
    formData.append('rating', ratingValue);

    var images = document.getElementById('images').files;
    if (images.length > 0) {
        for (var i = 0; i < images.length; i++) {
            formData.append('images', images[i]);
        }
    }

    // Gửi dữ liệu đánh giá đến server
    fetch('/api/reviews/' + storeId, {
        method: 'POST',
        body: formData
    })
    .then(response => {
        if (!response.ok) {
            // Cố gắng phân tích phản hồi JSON để lấy thông báo lỗi
            return response.json().then(errData => {
                alert(errData.error || 'Có lỗi xảy ra khi gửi đánh giá!'); // Hiển thị thông báo lỗi
            });
        }
        return response.json(); // Trả về dữ liệu phản hồi nếu thành công
    })
    .then(data => {
        console.log('Thành công:', data);
        alert('Đánh giá đã được gửi thành công!'); // Thông báo gửi đánh giá thành công
        // Có thể reset form hoặc thực hiện các cập nhật UI khác ở đây
    })
    .catch(error => {
        console.error('Lỗi:', error);
        alert('Lỗi khi gửi đánh giá!'); // Thông báo lỗi chung
    });
}

// Thêm sự kiện lắng nghe cho form để ngăn chặn gửi mặc định và xử lý việc gửi
document.addEventListener('DOMContentLoaded', function() {
    var form = document.getElementById('reviewForm');
    form.addEventListener('submit', function(event) {
        event.preventDefault(); // Ngăn chặn gửi form mặc định
        submitReviewForm(); // Gọi hàm để gửi form
    });
});
