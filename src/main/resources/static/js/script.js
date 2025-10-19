async function enviarSave() {
    const jogo = document.getElementById('jogo').value;
    const descricao = document.getElementById('descricao').value;
    const file = document.getElementById('file').files[0];
    const capa = document.getElementById('image').files[0];
    const mensagem = document.getElementById('mensagem');

    if (!jogo || !descricao || !file || !capa) {
        mensagem.textContent = 'Preencha todos os campos.';
        mensagem.classList.add('has-text-danger');
        return;
    }

    const formData = new FormData();
    formData.append("jogo", jogo);
    formData.append("descricao", descricao);
    formData.append("file", file);
    formData.append("capa", capa);

    try {
        const response = await fetch('/saves/upload', {
            method: 'POST',
            body: formData
        });

        if (response.status === 401) {
          console.error('Unauthorized: Please log in.');
          window.location.href = '/login';
        }

        if (response.ok) {
            mensagem.textContent = 'Save enviado com sucesso!';
            mensagem.classList.remove('has-text-danger');
            mensagem.classList.add('has-text-success');

            getSaves();
        } else {
            mensagem.textContent = 'Erro ao enviar o save.';
            mensagem.classList.add('has-text-danger');
        }
    } catch (error) {
        console.error(error);
        mensagem.textContent = 'Erro inesperado.';
        mensagem.classList.add('has-text-danger');
    }
}

async function getSaves() {
    try {
        const response = await fetch('/saves/user', {
            method: 'GET'
        });

        if (response.status === 401) {
          console.error('Unauthorized: Please log in.');
          window.location.href = '/login';
          }

        if (response.ok) {
            const saves = await response.json();
            const feed = document.getElementById('feedSaves');
            feed.innerHTML = '';

            if (!saves || saves.lenght === 0) {
                return;
            }

            saves.forEach(save => {
                const saveCard = document.createElement('div');
                saveCard.className = 'box';
                saveCard.innerHTML = `
                    <article class="media">
                        <figure class="media-left">
                            <p class="image is-128x128">
                                <img src="${save.imagePath}" alt="Game Cover">
                            </p>
                        </figure>
                        <div class="media-content">
                            <div class="content">
                                <p>
                                    <strong>${save.jogo}</strong><br>
                                    ${save.descricao}<br>
                                    <small>👤 por <strong>${save.user.username}</strong></small>
                                </p>
                            </div>
                            <nav class="level is-mobile mt-3">
                                <div class="level-left">
                                    <a class="level-item ml-auto has-text-success" href="${save.path}" download>
                                        <button class="button is-link">Baixar</button>
                                    </a>
                                    <button class="button is-danger delete-button" data-id="${save.id}">Deletar</button>
                                </div>
                            </nav>
                        </div>
                    </article>
                `;
                feed.appendChild(saveCard);

                const deleteBtn = saveCard.querySelector('.delete-button');
                deleteBtn.addEventListener('click', function () {
                    const id = this.getAttribute('data-id');
                    deletarSave(id);
                });

            });

        } else {
            console.error('Erro ao buscar os saves.');
        }
    } catch (error) {
        console.error('Erro na requisição dos saves:', error);
    }
}

function switchTheme() {
    const html = document.documentElement;
    const theme = html.getAttribute('data-theme');

    if (theme === 'dark') {
        html.setAttribute('data-theme', 'light');
    } else {
        html.setAttribute('data-theme', 'dark');
    }
}

function deleteAlert(mensagem, tipo = "is-info") {
    let container = document.getElementById("notificacoes");
    if (!container) {
        container = document.createElement("div");
        container.id = "notificacoes";
        container.style.position = "fixed";
        container.style.top = "20px";
        container.style.right = "20px";
        container.style.zIndex = "1000";
        document.body.appendChild(container);
    }

    const notif = document.createElement("div");
    notif.className = `notification ${tipo}`;
    notif.innerHTML = `<button class="delete"></button> ${mensagem}`;

    notif.querySelector(".delete").onclick = () => notif.remove();

    container.appendChild(notif);

    setTimeout(() => notif.remove(), 6000);
}

function deletarSave(id) {
    fetch(`/saves?id=${id}`, { method: 'DELETE' })
        .then(response => {
            if (response.ok) {
                deleteAlert("✅ Save deletado com sucesso!", "is-success");
                getSaves();
            } else {
                deleteAlert("❌ Erro ao deletar o save.", "is-danger");
            }
        })
        .catch(() => bulmaAlert("⚠️ Erro de conexão com o servidor.", "is-warning"));
}

document.addEventListener('DOMContentLoaded', function () {
    getSaves();
});
