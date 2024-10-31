let selectedReviewId = null;

// Hàm để mở modal và lưu lại reviewId
function openReportModal(event, button) {
    const reviewId = button.getAttribute('data-review-id');
    const reportModal = document.getElementById('reportModal');

    // Cập nhật selectedReviewId
    selectedReviewId = reviewId; // Cập nhật ID của review

    // Kiểm tra nếu modal báo cáo đã mở và đóng nó nếu cần
    if (!reportModal.classList.contains('hidden')) {
        closeReportModal();
    }

    reportModal.classList.remove('hidden'); // Hiển thị modal báo cáo
    document.body.classList.add('modal-open'); 
}

// Đóng modal
function closeReportModal() {
    const reportModal = document.getElementById('reportModal');
    reportModal.classList.add('hidden'); // Thêm lớp 'hidden' để ẩn modal
    document.body.classList.remove('modal-open'); // Gỡ bỏ lớp khi đóng modal
}

// Gửi báo cáo
function submitReport() {
    const reportReason = document.querySelector('input[name="reportReason"]:checked');

    // Kiểm tra xem có lý do nào đã được chọn không
    if (!reportReason) {
        alert("Bạn cần chọn một lý do để báo cáo.");
        return; // Dừng lại nếu không có lý do được chọn
    }

    console.log("Đã chọn lý do:", reportReason.value); // Log lý do đã chọn

    // Kiểm tra reviewId
    if (!selectedReviewId) {
        alert("Không có review ID để gửi báo cáo.");
        return;
    }

    // Dữ liệu báo cáo
    const reportData = {
        reviewId: selectedReviewId,
        reason: reportReason.value
    };

    console.log("Gửi dữ liệu báo cáo:", reportData); // Log dữ liệu trước khi gửi

    // Gửi dữ liệu đến API
    fetch('/report/create', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(reportData),
    })
    .then(response => {
        if (response.ok) {
            alert("Báo cáo đã được gửi thành công!");
            closeReportModal(); // Đóng modal sau khi gửi thành công
        } else {
            return response.text().then(errorText => {
                alert(`Có lỗi xảy ra: ${errorText}`);
            });
        }
    })
    .catch((error) => {
        console.error('Lỗi:', error); // Log lỗi khi gửi báo cáo
        alert("Có lỗi xảy ra khi gửi báo cáo!");
    });
}

// Đảm bảo rằng khi DOM đã sẵn sàng, mọi thứ được cấu hình
document.addEventListener("DOMContentLoaded", function() {
    console.log("DOM đã sẵn sàng."); // Log khi DOM sẵn sàng
}); 
