const monthGamesContainer = document.getElementById('month-game');

fetch('/api/month-games')
  .then(response => {
    if (!response.ok) {
      throw new Error('Erro ao buscar jogos do mês');
    }
    return response.json();
  })
  .then(games => {
    games.slice(0, 5).forEach(game => {
      const html = `
        <div class="bg-dark rounded d-flex flex-row align-items-center justify-content-between p-3 mt-3"
             style="height: 100px; width:100%">

            <!-- Time da Esquerda -->
            <div class="d-flex flex-row align-items-center" style="width: 40%;">
                <div style="width: 40%">
                    <img src="${game.homeImage}" alt="${game.homeTeam}" style="width: 48px; height: 48px;">
                </div>
                <div style="width: 60%">
                    <strong class="fs-3 text-white ms-6">${game.homeTeam}</strong>
                </div>
            </div>

            <!-- VS -->
            <div class="d-flex align-items-center justify-content-center" style="width: 20%;">
                <img src="/img/versus.png" alt="Versus" style="width: 70px; height: 70px;">
            </div>

            <!-- Time da Direita -->
            <div class="d-flex flex-row align-items-center" style="width: 40%;">
                <div style="width: 60%">
                    <strong class="fs-3 text-white ms-6">${game.awayTeam}</strong>
                </div>
                <div style="width: 40%">
                    <img src="${game.awayImage}" alt="${game.awayTeam}" style="width: 48px; height: 48px;">
                </div>
            </div>
        </div>
      `;
      monthGamesContainer.insertAdjacentHTML('beforeend', html);
    });
  })
  .catch(error => {
    console.error("Erro ao carregar jogos:", error);
    monthGamesContainer.innerHTML = "<p class='text-danger'>Erro ao carregar os jogos do mês.</p>";
  });
