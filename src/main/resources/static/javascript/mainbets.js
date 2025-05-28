const bestBetsContainer = document.getElementById('main-bets');

// Busca as apostas com maior valor do backend
fetch("api/top-bets")
  .then(response => response.json())
  .then(bets => {
    // Referência ao container
    const bestBetsContainer = document.getElementById("main-bets");

    if (!bets.length) {
          // Se a lista estiver vazia, mostra a mensagem
          bestBetsContainer.innerHTML = `
            <div class="text-center mt-4">
              <img src="/img/no-data.jpg" alt="Sem dados" style="width: 450px;height: 450px">
              <p class="fs-5 fw-bold">Não foram encontrados dados.</p>
            </div>
          `;
          return;
    }

    // Máximo 6 apostas
    bets.slice(0, 6).forEach(bet => {
      const html = `
        <div class="d-flex justify-content-between align-items-center rounded p-2 mb-2" style="min-width: 460px; height: 90px;">
          <!-- Imagem à esquerda -->
          <div class="d-flex align-items-center" style="flex-shrink: 0;">
            <img src="${bet.winnerImage}" alt="Time" style="width: 80px; height: 80px;">
          </div>

          <!-- texto centralizado -->
          <div class="d-flex align-items-center flex-grow-1 justify-content-center px-3" style="height: 100%;">
            <strong class="fs-6 m-0 text-wrap text-center">${bet.myTeam} vence ${bet.otherTeam}</strong>
          </div>

          <!-- Bloco de Odd, value e Botões -->
          <div class="position-relative text-white rounded-pill px-3 py-2 d-flex flex-column align-items-center justify-content-center"
               style="background-color: #183B4E;">
                <div class="position-absolute top-0 translate-middle-y text-light px-2 rounded-pill small"
                     style="background-color: #27548A;">
                    Odd ${bet.odd}
                </div>

                <div class="fw-bold mt-2">Valor: R$ ${bet.value}</div>

                <div class="d-flex justify-content-between w-100 mt-2 px-1">
                  <button type="button" class="icon-btn text-danger border border-danger bg-white rounded-circle px-2"
                        onclick="updateBet(${bet.guessId}, 0)">
                    &#10006;
                  </button>
                  <button type="button" class="icon-btn text-success border border-success bg-white rounded-circle px-2"
                        onclick="updateBet(${bet.guessId}, 1)">
                    &#10004;
                  </button>
                </div>
          </div>
        </div>
      `;
      bestBetsContainer.insertAdjacentHTML('beforeend', html);
    });
  })
  .catch(error => {
    bestBetsContainer.innerHTML = `
      <div class="text-center mt-4">
        <img src="/img/no-data.jpg" alt="Sem dados" style="width: 450px;height: 450px">
        <p class="fs-5 fw-bold">Não foram encontrados dados.</p>
      </div>
    `;
  });
