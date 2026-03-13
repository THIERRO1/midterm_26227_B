// =============================================
// AIPS — Academic Integrity & Plagiarism System
// Main JavaScript
// =============================================

document.addEventListener('DOMContentLoaded', () => {

    // ─── Auto-dismiss alert messages after 5 seconds ───
    document.querySelectorAll('.alert').forEach(alert => {
        setTimeout(() => {
            alert.style.transition = 'opacity 0.4s ease, transform 0.4s ease';
            alert.style.opacity = '0';
            alert.style.transform = 'translateY(-8px)';
            setTimeout(() => alert.remove(), 400);
        }, 5000);
    });

    // ─── Animate similarity bars on load ───
    // Set width to 0 first, then animate to real value
    document.querySelectorAll('.similarity-bar-fill').forEach(bar => {
        const realWidth = bar.style.width;
        bar.style.width = '0%';
        requestAnimationFrame(() => {
            setTimeout(() => {
                bar.style.transition = 'width 1.2s cubic-bezier(0.4, 0, 0.2, 1)';
                bar.style.width = realWidth;
            }, 200);
        });
    });

    // ─── Animate stat card numbers (count-up effect) ───
    document.querySelectorAll('.stat-value').forEach(el => {
        const target = parseInt(el.textContent, 10);
        if (isNaN(target) || target === 0) return;

        let start = 0;
        const duration = 800;
        const step = Math.ceil(target / (duration / 16));

        const timer = setInterval(() => {
            start = Math.min(start + step, target);
            el.textContent = start;
            if (start >= target) clearInterval(timer);
        }, 16);
    });

    // ─── File upload drag-and-drop ───
    const uploadArea = document.querySelector('.file-upload-area');
    if (uploadArea) {
        ['dragenter', 'dragover'].forEach(evt => {
            uploadArea.addEventListener(evt, (e) => {
                e.preventDefault();
                uploadArea.style.borderColor = 'var(--blue)';
                uploadArea.style.background = '#eff6ff';
            });
        });

        ['dragleave', 'drop'].forEach(evt => {
            uploadArea.addEventListener(evt, (e) => {
                e.preventDefault();
                uploadArea.style.borderColor = '';
                uploadArea.style.background = '';
                if (evt === 'drop') {
                    const files = e.dataTransfer.files;
                    const input = document.getElementById('fileInput');
                    if (input && files.length) {
                        input.files = files;
                        const display = document.getElementById('fileNameDisplay');
                        if (display) {
                            display.textContent = '📄 ' + files[0].name;
                            display.style.display = 'block';
                        }
                    }
                }
            });
        });
    }

    // ─── Active nav item highlight ───
    const currentPath = window.location.pathname;
    document.querySelectorAll('.nav-item').forEach(item => {
        const href = item.getAttribute('href');
        if (href && currentPath.startsWith(href) && href !== '/') {
            item.classList.add('active');
        }
    });

    // ─── Mobile sidebar toggle ───
    const sidebarToggle = document.getElementById('sidebarToggle');
    const sidebar = document.querySelector('.sidebar');
    if (sidebarToggle && sidebar) {
        sidebarToggle.addEventListener('click', () => {
            sidebar.classList.toggle('open');
        });
    }

    console.log('✅ AIPS System loaded successfully');
});
