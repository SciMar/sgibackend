let idx = 0;

function moveSlide(step) {
    const track = document.querySelector('.carousel-track');
    const total = document.querySelectorAll('.carousel-img').length;

    idx = (idx + step + total) % total;

    track.style.transform = `translateX(${-idx * 100}%)`;
}

