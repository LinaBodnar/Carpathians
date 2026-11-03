document.addEventListener('DOMContentLoaded', () => {

    // === 1. ДАНІ МАРШРУТІВ ===
    const routesData = {
        'SHPITSI': {
            title: 'МАРШРУТ: ШПИЦІ',
            mapImg: 'images/shpitsi.bmp',
            interactiveUrl: 'https://mapy.cz/s/dekodejapa',
            difficulty: 'Середній',
            length: '24 км',
            duration: '1 день',
            points: [
                { title: 'Старт: Заросляк', alt: '1330м', desc: 'Тихий початок серед смерек і туману. Тут ліс дихає, а стежка тільки народжується.', img: 'images/zaroslyak.bmp' },
                { title: 'Озеро Несамовите', alt: '1750м', desc: 'Озеро легенд, де вітер шепоче давні історії. Вода як дзеркало неба.', img: 'images/nesamovyte.bmp' },
                { title: 'Гора Шпиці', alt: '1863м', desc: 'Скелясті піки, немов кам\'яні вартові. Тут панує тиша, вітер і велич.', img: 'images/shpitsi2.bmp' },
                { title: 'Повернення', alt: '', desc: 'Спуск крізь світло і тіні. Повернення з гір схоже на пробудження від сну.', img: 'images/return.bmp' }
            ]
        },
        'PIPIVAN': {
            title: 'МАРШРУТ: ПІП ІВАН',
            mapImg: 'images/pip ivan.bmp',
            interactiveUrl: 'https://mapy.cz/s/bebojufede',
            difficulty: 'Важкий',
            length: '32 км',
            duration: '1 день',
            points: [
                { title: 'Старт: Дземброня', alt: '896м', desc: 'Село на краю світу, де тиша готує до висоти.', img: 'images/dzembronya.bmp' },
                { title: 'Вухатий Камінь', alt: '1864м', desc: 'Кам\'яний велетень, що стоїть на варті.', img: 'images/vukh_kamen.bmp' },
                { title: 'Гора Піп Іван', alt: '2022м', desc: 'Обсерваторія в хмарах, де час зупиняється.', img: 'images/pip_ivan.bmp' },
                { title: 'Гора Смотрич', alt: '1898м', desc: 'Гора, що мовчки споглядає світ.', img: 'images/smotrych.bmp' },
                { title: 'Повернення', alt: '', desc: 'Спуск, наче повернення з іншої реальності.', img: 'images/return_dzembronya.bmp' }
            ]
        },
        'HOVERLA': {
            title: 'МАРШРУТ: ГОВЕРЛА',
            mapImg: 'images/hoverla.bmp',
            interactiveUrl: 'https://mapy.cz/s/dojevagata',
            difficulty: 'Середній',
            length: '10 км',
            duration: '1 день',
            points: [
                { title: 'Старт: Заросляк', alt: '1330м', desc: 'Початок серед ялин, де стартує шлях до найвищої точки України.', img: 'images/zaroslyak.bmp' },
                { title: 'Мала Говерла', alt: '1762м', desc: 'Маленька вершина перед великою, як пролог до епічного сходження.', img: 'images/mala_hoverla.bmp' },
                { title: 'Гора Говерла', alt: '2061м', desc: 'Найвища точка, де вітер говорить мовою свободи.', img: 'images/hoverla1.bmp' },
                { title: 'Водоспад', alt: '', desc: 'Водоспад співає пісню гір — дзвінку та живу.', img: 'images/hoverla_waterfall.bmp' },
                { title: 'Повернення', alt: '', desc: 'Спуск з висоти, де кожен крок — це повернення до себе.', img: 'images/return_hoverla.bmp' }
            ]
        },
        'SVYDOVETS': {
            title: 'МАРШРУТ: СВИДОВЕЦЬ',
            mapImg: 'images/svydovets.bmp',
            interactiveUrl: 'https://mapy.cz/s/jotolemezo',
            difficulty: 'Середній',
            length: '18 км',
            duration: '1 день',
            points: [
                { title: 'Старт: Драгобрат', alt: '1707м', desc: 'Початок серед хмар і підйомників — ворота у світ хребта.', img: 'images/dragobrat.bmp' },
                { title: 'Гора Стіг', alt: '1704м', desc: 'Гора на підйомнику — легкий старт і панорама на весь Свидовець.', img: 'images/stih.bmp' },
                { title: 'Гора Крачунеска', alt: '1686м', desc: 'Тиха вершина, де вітер розповідає історії.', img: 'images/krachuneska.bmp' },
                { title: 'Гора Великий Котел', alt: '1771м', desc: 'Котел хмар і світла — місце сили і простору.', img: 'images/kotel.bmp' },
                { title: 'Озеро Ворожеска', alt: '1460м', desc: 'Озеро, як очі гір — глибоке, спокійне, чарівне.', img: 'images/vorozeska_lake.bmp' },
                { title: 'Гора Ворожеска', alt: '1731м', desc: 'Вершина над озером — погляд на шлях і серце хребта.', img: 'images/vorozeska_mountain.bmp' },
                { title: 'Гора Герашаска', alt: '1762м', desc: 'Гора, що дарує спокій і рівновагу.', img: 'images/herashasyka.bmp' },
                { title: 'Гора Догяска', alt: '1761м', desc: 'Фінальна вершина — кінець хребта і початок спуску.', img: 'images/doghiaska.bmp' },
                { title: 'Повернення', alt: '', desc: 'Повернення в цивілізацію — з душею, наповненою горами.', img: 'images/return_dragobrat.bmp' }
            ]
        },
        'GORGANY': {
            title: 'МАРШРУТ: ҐОРҐАНИ',
            mapImg: 'images/gorgany.bmp',
            interactiveUrl: 'https://mapy.cz/s/notaleguma',
            difficulty: 'Важкий',
            length: '16 км',
            duration: '1 день',
            points: [
                { title: 'Старт: Яремче', alt: '585м', desc: 'Місто біля підніжжя гір — тут починається пригода.', img: 'images/yaremche.bmp' },
                { title: 'Явірник-Ґорґан', alt: '1467м', desc: 'Кам\'яниста вершина, що зберігає дух Ґорґан.', img: 'images/yavirnyk_gorgan.bmp' },
                { title: 'Полонина Явірник', alt: '~1200м', desc: 'Гірська полонина, де шепочуть трави, а простір відкриває серце.', img: 'images/polony_yavirnyk.bmp' },
                { title: 'Гора Круглий Явірник', alt: '1222м', desc: 'Округла вершина — м\'яка і затишна, як обійми природи.', img: 'images/round_yavirnyk.bmp' },
                { title: 'Фініш: Татарів', alt: '750м', desc: 'Фініш у лісі — спокійне завершення гірської симфонії.', img: 'images/tatriv.bmp' }
            ]
        },
        'KUKUL': {
            title: 'МАРШРУТ: КУКУЛ',
            mapImg: 'images/kukul.bmp',
            interactiveUrl: 'https://mapy.cz/s/mufevozeve',
            difficulty: 'Легкий',
            length: '14 км',
            duration: '1 день',
            points: [
                { title: 'Старт: Лазещина', alt: '706м', desc: 'Село, де починається шлях до тиші та висоти.', img: 'images/lazeshchyna.bmp' },
                { title: 'Гора Верхи-Дебра', alt: '1237м', desc: 'Гора, що відкриває перші панорами і готує до підйому.', img: 'images/verkhi_debra.bmp' },
                { title: 'Гора Під-Берди', alt: '1388м', desc: 'Тиха вершина, де ліс поступається місцем простору.', img: 'images/pid_berdy.bmp' },
                { title: 'Гора Кукул', alt: '1539м', desc: 'Кукул — вершина з душею, де вітер говорить мовою свободи.', img: 'images/kukul1.bmp' },
                { title: 'Повернення', alt: '', desc: 'Спуск у знайоме, з новим поглядом і миром у серці.', img: 'images/return_lazeshchyna.bmp' }
            ]
        },
        'BREBENESKUL': {
            title: 'МАРШРУТ: БРЕБЕНЕСКУЛ',
            mapImg: 'images/brebenescul.bmp',
            interactiveUrl: 'https://mapy.cz/s/mesopuhuko',
            difficulty: 'Важкий',
            length: '28 км',
            duration: '1-2 дні',
            points: [
                { title: 'Старт: Заросляк', alt: '1330м', desc: 'Старт у знайомому лісі — ворота у високогірну подорож.', img: 'images/zaroslyak.bmp' },
                { title: 'Гора Пожижевська', alt: '1822м', desc: 'Гора, що відкриває перші види і готує до справжніх висот.', img: 'images/pozyzhevska.bmp' },
                { title: 'Гора Данциж', alt: '1855м', desc: 'Скеляста вершина, де вітер говорить мовою хребта.', img: 'images/danzig.bmp' },
                { title: 'Гора Туркул', alt: '1933м', desc: 'Гора з панорамою на весь Чорногірський масив.', img: 'images/turkul.bmp' },
                { title: 'Гора Ребра', alt: '2001м', desc: 'Гострі хребти гір — як хребет карпатської сили.', img: 'images/rebra.bmp' },
                { title: 'Гора Бребенескул', alt: '2038м', desc: 'Найвища точка маршруту — тиша, простір і велич.', img: 'images/brebeneskul1.bmp' },
                { title: 'Озеро Бребенескул', alt: '1801м', desc: 'Озеро під вершиною — дзеркало неба і спокою.', img: 'images/lake_brebeneskul.bmp' },
                { title: 'Озеро Несамовите', alt: '1750м', desc: 'Озеро Легенд — завершення високогірної дуги.', img: 'images/nesamovyte.bmp' },
                { title: 'Повернення', alt: '', desc: 'Спуск у ліс — повернення з хмар до коріння.', img: 'images/return.bmp' }
            ]
        },
        'GUTIN': {
            title: 'МАРШРУТ: ГУТИН ТОМНАТИК',
            mapImg: 'images/gutin tomnatik.bmp',
            interactiveUrl: 'https://mapy.cz/s/janorunara',
            difficulty: 'Важкий',
            length: '20 км',
            duration: '1 день',
            points: [
                { title: 'Старт: Заросляк', alt: '1330м', desc: 'Старт у знайомому лісі — тиша, що готує до висоти.', img: 'images/zaroslyak.bmp' },
                { title: 'Озеро Несамовите', alt: '1750м', desc: 'Озеро Легенд — дзеркало неба і спокою.', img: 'images/nesamovyte.bmp' },
                { title: 'Гора Ребра', alt: '2001м', desc: 'Гострі хребти спини — як хребет Карпат.', img: 'images/rebra.bmp' },
                { title: 'Гора Гутин Томнатик', alt: '2016м', desc: 'Вершина, де вітер говорить мовою свободи.', img: 'images/gutin.bmp' },
                { title: 'Повернення', alt: '', desc: 'Спуск у ліс — завершення кола, сповненого простором і тишею.', img: 'images/return.bmp' }
            ]
        }
    };

    // === 2. ЕЛЕМЕНТИ ===
    const routeTabs = document.querySelectorAll('#route-tabs button');
    const accordionContainer = document.getElementById('routeAccordion');
    const currentMapImg = document.getElementById('current-map-img');
    const currentRouteTitle = document.getElementById('current-route-title');
    const routeDifficulty = document.getElementById('route-difficulty');
    const routeLength = document.getElementById('route-length');
    const routeDuration = document.getElementById('route-duration');
    const interactiveMapBtn = document.getElementById('open-interactive-map');
    const mapyIframe = document.getElementById('mapy-iframe');
    const formRouteSelect = document.getElementById('form-route-select');
    const localPhotoModal = new bootstrap.Modal(document.getElementById('localPhotoModal'));
    const modalPointImg = document.getElementById('modal-point-img');
    const modalPointTitle = document.getElementById('modal-point-title');

    // Елементи меню
    const mobileMenuElement = document.getElementById('mobileMenu');
    const logoTrigger = document.getElementById('logo-trigger');

    // === ЛОГІКА МОБІЛЬНОГО МЕНЮ ===
    if (logoTrigger && mobileMenuElement) {
        const bsOffcanvas = new bootstrap.Offcanvas(mobileMenuElement);

        // Обробник кліку на логотипі для відкриття offcanvas на мобільних
        logoTrigger.addEventListener('click', (e) => {
            if (window.innerWidth < 992) {
                e.preventDefault();
                bsOffcanvas.toggle();
            }
        });

        // Обробник кліків для мобільних навігаційних посилань (якорів)
        document.querySelectorAll('.mobile-nav-link').forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                const targetId = link.getAttribute('href');

                bsOffcanvas.hide();

                document.querySelector(targetId).scrollIntoView({ behavior: 'smooth' });
            });
        });
    }

    // === 3. ЗАВАНТАЖЕННЯ МАРШРУТУ ===
    function loadRoute(routeKey) {
        const data = routesData[routeKey];
        if (!data) return;

        currentMapImg.style.opacity = '0';
        setTimeout(() => {
            currentMapImg.src = data.mapImg;
            currentMapImg.style.opacity = '1';
        }, 200);

        currentRouteTitle.textContent = data.title;
        routeDifficulty.textContent = `Складність: ${data.difficulty}`;
        routeLength.textContent = `Довжина: ${data.length}`;
        routeDuration.textContent = `Час: ${data.duration}`;

        interactiveMapBtn.onclick = () => {
            mapyIframe.src = data.interactiveUrl;
            const mapModal = new bootstrap.Modal(document.getElementById('interactiveMapModal'));
            mapModal.show();
        };

        formRouteSelect.innerHTML = '';
        Object.keys(routesData).forEach(key => {
            const option = document.createElement('option');
            option.value = key;
            option.textContent = routesData[key].title.replace('МАРШРУТ: ', '');
            option.style.color = '#000';
            if (key === routeKey) option.selected = true;
            formRouteSelect.appendChild(option);
        });

        accordionContainer.innerHTML = '';
        data.points.forEach((point, index) => {
            const itemId = `collapse-${index}`;
            const headerId = `heading-${index}`;

            const itemHTML = `
                <div class="accordion-item">
                    <h2 class="accordion-header" id="${headerId}">
                        <button class="accordion-button ${index !== 0 ? 'collapsed' : ''}" type="button" data-bs-toggle="collapse" data-bs-target="#${itemId}">
                            <div class="d-flex align-items-center w-100 pe-3">
                                <div class="badge bg-success rounded-circle p-2 me-3" style="width:30px;height:30px;display:flex;justify-content:center;align-items:center;">${index + 1}</div>
                                <span class="fw-bold">${point.title}</span>
                                ${point.alt ? `<span class="badge bg-dark ms-auto border border-secondary">${point.alt}</span>` : ''}
                            </div>
                        </button>
                    </h2>
                    <div id="${itemId}" class="accordion-collapse collapse ${index === 0 ? 'show' : ''}" data-bs-parent="#routeAccordion">
                        <div class="accordion-body">
                            <p class="fst-italic mb-3">${point.desc}</p>
                            <button class="btn btn-sm btn-outline-light view-photo-btn w-100" data-img="${point.img}" data-title="${point.title}">
                                <i class="bi bi-image me-2"></i> Показати фото
                            </button>
                        </div>
                    </div>
                </div>
            `;
            accordionContainer.insertAdjacentHTML('beforeend', itemHTML);
        });

        document.querySelectorAll('.view-photo-btn').forEach(btn => {
            btn.addEventListener('click', function () {
                modalPointImg.src = this.getAttribute('data-img');
                modalPointTitle.textContent = this.getAttribute('data-title');
                localPhotoModal.show();
            });
        });
    }

    // === 4. TABS CLICK ===
    routeTabs.forEach(tab => {
        tab.addEventListener('click', function () {
            const routeKey = this.getAttribute('data-route');
            loadRoute(routeKey);
        });
    });

    // === 5. GALLERY SCROLL LOGIC  ===
    const scrollContainer = document.getElementById('galleryScroll');
    const leftBtn = document.querySelector('.gallery-nav-btn.left');
    const rightBtn = document.querySelector('.gallery-nav-btn.right');
    const fullscreenModal = new bootstrap.Modal(document.getElementById('fullscreenGalleryModal'));
    const fullscreenImg = document.getElementById('fullscreen-gallery-img');

    const SCROLL_AMOUNT = 300;
    let scrollInterval;
    let isMouseOver = false;

    if (scrollContainer && leftBtn && rightBtn) {
        const items = Array.from(scrollContainer.querySelectorAll('.gallery-item'));
        const originalCount = items.length;

        const clonesToAppend = items.slice(0, 3).map(item => item.cloneNode(true));
        const clonesToPrepend = items.slice(-3).map(item => item.cloneNode(true));

        clonesToPrepend.reverse().forEach(item => {
            item.classList.add('cloned');
            scrollContainer.insertBefore(item, scrollContainer.firstChild);
        });
        clonesToAppend.forEach(item => {
            item.classList.add('cloned');
            scrollContainer.appendChild(item);
        });

        const offset = clonesToPrepend.length * SCROLL_AMOUNT;

        scrollContainer.scrollLeft = offset;

        const checkCycle = () => {
            if (scrollContainer.scrollLeft < offset) { 
                scrollContainer.scrollLeft = offset + (originalCount * SCROLL_AMOUNT);
            }
            else if (scrollContainer.scrollLeft >= offset + (originalCount * SCROLL_AMOUNT)) {
                scrollContainer.scrollLeft = offset;
            }
        };

        const scrollForward = () => {
            scrollContainer.scrollBy({ left: SCROLL_AMOUNT, behavior: 'smooth' });
            setTimeout(checkCycle, 300);
        };

        const startAutoScroll = () => {
            if (scrollInterval) clearInterval(scrollInterval);
            scrollInterval = setInterval(() => {
                if (!isMouseOver) {
                    scrollForward();
                }
            }, 3000);
        };

        scrollContainer.parentNode.addEventListener('mouseenter', () => { isMouseOver = true; });
        scrollContainer.parentNode.addEventListener('mouseleave', () => { isMouseOver = false; });

        scrollContainer.addEventListener('scroll', checkCycle);

        leftBtn.addEventListener('click', () => {
            scrollContainer.scrollBy({ left: -SCROLL_AMOUNT, behavior: 'smooth' });
            startAutoScroll();
        });
        rightBtn.addEventListener('click', () => {
            scrollContainer.scrollBy({ left: SCROLL_AMOUNT, behavior: 'smooth' });
            startAutoScroll();
        });

        startAutoScroll();
    }

    document.querySelectorAll('.gallery-item').forEach(item => {
        item.addEventListener('click', function () {
            const imgSrc = this.getAttribute('data-img');
            fullscreenImg.src = imgSrc;
            fullscreenModal.show();
        });
    });

    // === 6. FOOTER CLICK ===
    document.querySelectorAll('.footer-links a').forEach(link => {
        link.addEventListener('click', function (e) {
            e.preventDefault();
            const routeKey = this.getAttribute('data-footer-route');
            document.getElementById('route-selection').scrollIntoView({ behavior: 'smooth' });
            const targetTab = document.querySelector(`button[data-route="${routeKey}"]`);
            if (targetTab) {
                const bsTab = new bootstrap.Tab(targetTab);
                bsTab.show();
                loadRoute(routeKey);
            }
        });
    });

    // === 7. FORM SUBMIT ===
    const bookingForm = document.getElementById('booking-form');
    if (bookingForm) {
        bookingForm.addEventListener('submit', function (e) {
            e.preventDefault();
            const successModal = new bootstrap.Modal(document.getElementById('successModal'));
            successModal.show();
            bookingForm.reset();
        });
    }

    // === 8. MASKED INPUT FOR PHONE ===
    const phoneInput = document.getElementById('phone-input');
    if (phoneInput) {
        IMask(phoneInput, {
            mask: '+{38} (000) 000-00-00',
            lazy: false,
            placeholderChar: '_'
        });
    }

    loadRoute('SHPITSI');

    // === 9. UI ===
    const progressBar = document.getElementById('progress-bar');
    const backToTopBtn = document.getElementById('back-to-top');

    window.addEventListener('scroll', () => {
        const scrollTop = document.documentElement.scrollTop;
        const height = document.documentElement.scrollHeight - document.documentElement.clientHeight;
        const scrolled = (scrollTop / height) * 100;
        progressBar.style.width = scrolled + '%';

        if (scrollTop > 400) backToTopBtn.style.display = 'flex';
        else backToTopBtn.style.display = 'none';
    });

    backToTopBtn.addEventListener('click', () => {
        window.scrollTo({ top: 0, behavior: 'smooth' });
    });
});