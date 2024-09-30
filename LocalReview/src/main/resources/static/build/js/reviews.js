// Lấy tất cả các ngôi sao
const stars = document.querySelectorAll('.star');
const ratingValue = document.getElementById('ratingValue');
const submitButton = document.getElementById('submitButton');
const loadingMessage = document.getElementById('loadingMessage');

// Khi click vào ngôi sao, cập nhật điểm đánh giá
stars.forEach((star, index) => {
    star.addEventListener('click', () => {
        ratingValue.value = index + 1;

        stars.forEach((s, i) => {
            s.classList.toggle('text-yellow-400', i <= index); // Đánh dấu ngôi sao đã chọn
            s.classList.toggle('text-gray-400', i > index); // Ngôi sao chưa chọn vẫn giữ màu xám
        });
    });
});

// Hàm để lấy Store ID từ URL
function getStoreId() {
    const url = window.location.href;
    const matches = url.match(/\/store\/detail\/([a-zA-Z0-9-]+)$/);
    return matches ? matches[1] : null;
}

// Hàm để gửi đánh giá
function submitReviewForm() {
    const storeId = getStoreId();
    if (!storeId) {
        return alert('ID cửa hàng không có!');
    }

    const formData = new FormData();
    const comment = document.getElementById('comment').value;
    const ratingValue = document.getElementById('ratingValue').value;

    // Xác thực nội dung và điểm đánh giá
    if (!comment) {
        return alert('Vui lòng nhập nội dung đánh giá.');
    }
    if (ratingValue === "0" || ratingValue === "undefined") {
        return alert('Vui lòng chọn điểm đánh giá từ 1 đến 5 sao.');
    }

    // Thêm dữ liệu vào FormData
    formData.append('comment', comment);
    formData.append('rating', ratingValue);

    const images = document.getElementById('images').files;
    for (const image of images) {
        formData.append('images', image);
    }

    // Hiển thị trạng thái "Đang gửi" và ẩn nút gửi
    submitButton.classList.add('hidden');
    loadingMessage.classList.remove('hidden');

    // Gửi dữ liệu đánh giá đến server
    fetch(`/api/reviews/${storeId}`, {
        method: 'POST',
        body: formData
    })
    .then(response => {
        if (!response.ok) throw new Error('Phản hồi mạng không hợp lệ');
        return response.json();
    })
    .then(data => {
        console.log('Thành công:', data);
        alert('Đánh giá đã được gửi thành công!');
        location.reload(); // Tự động tải lại trang
    })
    .catch(error => {
        console.error('Lỗi:', error);
        alert('Có lỗi xảy ra khi gửi đánh giá!');
    })
    .finally(() => {
        // Khôi phục lại trạng thái nút gửi và ẩn thông báo đang gửi
        submitButton.classList.remove('hidden');
        loadingMessage.classList.add('hidden');
    });
}


// Hàm để đóng modal
function closeModal() {
    document.getElementById('myModaldanhgia').classList.add('hidden');
    // Reset form values
    document.getElementById('reviewForm').reset();
    ratingValue.value = 0;
    stars.forEach(star => {
        star.classList.remove('text-yellow-400');
        star.classList.add('text-gray-400');
    });
}

// Hàm để cập nhật danh sách đánh giá (cần thực hiện)
function updateReviewList(newReviewData) {
    // Cập nhật danh sách đánh giá ở đây
    // Thêm mã để hiển thị đánh giá mới mà không cần tải lại trang
}

// Thêm sự kiện lắng nghe cho form
document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('reviewForm');
    form.addEventListener('submit', (event) => {
        event.preventDefault(); // Ngăn gửi form mặc định
        submitReviewForm(); // Gọi hàm gửi đánh giá
    });
});
