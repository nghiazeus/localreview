document.addEventListener('DOMContentLoaded', function () {
    const storeContainer = document.querySelector('#store-container .grid');
    const loadMoreTrigger = document.getElementById('load-more-trigger');
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                loadMoreStores();
            }
        });
    }, { rootMargin: '100px' });

    observer.observe(loadMoreTrigger);

    function loadMoreStores() {
        fetch('/api/stores')  // Thay đổi URL API theo đúng endpoint của bạn
            .then(response => response.json())
            .then(data => {
                data.stores.forEach(store => {
                    const storeCard = document.createElement('div');
                    storeCard.className = 'h-full w-full flex-shrink-0 md:p-2';
                    storeCard.innerHTML = `
                        <a href="/store/detail/${store.storeId}" class="block h-full 
                        bg-gradient-to-r from-pink-50 via-gray-50 to-purple-50 shadow-md
                        rounded-lg overflow-hidden">
                            <!-- Image -->
                            <img class="w-full h-48 md:h-60 object-cover"
                                src="${store.storeImage}"
                                alt="Store Image">

                            <!-- Information Container -->
                            <div class="p-4 flex flex-col space-y-2">
                                <!-- Store Name -->
                                <div class="font-semibold text-lg text-gray-900 dark:text-gray-100">
                                    <i class="bi bi-shop font-semibold mr-1 text-lg text-purple-500"></i>
                                    <span>${store.storeName}</span>
                                </div>

                                <!-- Address -->
                                <div class="flex items-center text-gray-700 dark:text-gray-300 text-sm">
                                    <i class="bi bi-geo-alt text-red-500 text-lg mr-1"></i>
                                    <span>${store.addressCity}</span>
                                </div>

                                <!-- Category -->
                                <div class="text-gray-700 dark:text-gray-300 text-sm">
                                    <i class="bi bi-tag text-blue-500 text-lg mr-1"></i>
                                    <span>${store.storeCategories.categoriesName}</span>
                                </div>

                                <!-- Rating -->
                                <div class="flex items-center text-gray-900 dark:text-gray-100 text-sm">
                                    <i class="bi bi-star-fill text-yellow-500 text-lg mr-1"></i>
                                    <span>${store.rating}</span>
                                </div>
                            </div>
                        </a>
                    `;
                    storeContainer.appendChild(storeCard);
                });
                observer.observe(loadMoreTrigger);  // Tiếp tục theo dõi trigger
            })
            .catch(error => console.error('Lỗi khi tải dữ liệu:', error));
    }
});
