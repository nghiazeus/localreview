// Function to extract store ID from the current URL
function getStoreId() {
    var url = window.location.href;
    var matches = url.match(/\/store\/detail\/([a-zA-Z0-9-]+)$/);
    return matches ? matches[1] : null;
}

// Function to handle form submission
function submitReviewForm() {
    var storeId = getStoreId();
    if (!storeId) {
        alert('Store ID is missing!');
        return;
    }

    var formData = new FormData();
    formData.append('comment', document.getElementById('comment').value);
    formData.append('rating', document.getElementById('rating').value);

    var images = document.getElementById('images').files;
    if (images.length > 0) {
        for (var i = 0; i < images.length; i++) {
            formData.append('images', images[i]);
        }
    }

    fetch('/api/reviews/' + storeId, {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        console.log('Success:', data);
        alert('Review submitted successfully!');
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Error submitting review!');
    });
}

// Add event listener to the form to prevent default submission and handle submission
document.addEventListener('DOMContentLoaded', function() {
    var form = document.getElementById('reviewForm');
    form.addEventListener('submit', function(event) {
        event.preventDefault();
        submitReviewForm();
    });
});
