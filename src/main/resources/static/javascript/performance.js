const statsContainer = document.getElementById('performance');

// Função para exibir mensagem de "sem dados" centralizada
function showNoData() {
  statsContainer.innerHTML = `
    <div class="d-flex justify-content-center align-items-center" style="height: 100%; width: 100%;">
      <div class="d-flex flex-column justify-content-center align-items-center" href="/settings">
        <img src="/img/no-data.jpg" alt="Sem dados" style="width: 150px; height: 150px;">
        <p class="fs-5 fw-bold mt-3">Não foram encontrados dados ou você não possui nível suficiente.</p>
      </div>
    </div>
  `;
}

fetch('/api/performance')
  .then(response => {
    if (!response.ok) {
      showNoData();
      return [];
    }
    return response.json();
  })
  .then(teams => {
    if (!teams.length) {
      showNoData();
      return;
    }

    const userRank = parseInt(document.getElementById('userRank').value, 10);
    if (isNaN(userRank) || userRank <= 1) {
      showNoData(); // já exibe a imagem de "sem dados"
      return;
    }

    const melhorTime = teams.reduce((max, time) => time.totalGreens > max.totalGreens ? time : max, teams[0]);
    const piorTime = teams.reduce((max, time) => time.totalReds > max.totalReds ? time : max, teams[0]);

    const totalGreens = teams.reduce((sum, time) => sum + time.totalGreens, 0);
    const totalReds = teams.reduce((sum, time) => sum + time.totalReds, 0);

    fetch('/api/performancestats')
      .then(response => {
        if (!response.ok) {
          showNoData();
          return null;
        }
        return response.json();
      })
      .then(performance => {
        if (!performance) return;

        const { totalAcertos, totalErros } = performance;
        const total = totalAcertos + totalErros;
        const percentualAcertos = total > 0 ? ((totalAcertos / total) * 100).toFixed(1) : 0;
        const percentualErros = total > 0 ? (100 - percentualAcertos).toFixed(1) : 0;

        const acertosNum = parseFloat(percentualAcertos);
        const errosNum = parseFloat(percentualErros);

        const acertosStyle = acertosNum >= errosNum ? 'fs-5 fw-bold' : 'fs-6 fw-normal';
        const errosStyle = errosNum > acertosNum ? 'fs-5 fw-bold' : 'fs-6 fw-normal';

        fetch(`/api/generate-dashboard?acertos=${totalAcertos}&erros=${totalErros}`, {
          method: 'POST'
        })
        .then(response => {
          if (!response.ok) throw new Error('Erro ao gerar o dashboard');

          const userId = document.getElementById('userId')?.value;
            if (!userId) {
              console.error('ID do usuário não encontrado!');
              return;
            }

          const html = `
            <div class="d-flex justify-content-center align-items-center gap-5 flex-wrap w-100">
              <div class="d-flex flex-column align-items-center" style="width: 250px;">
                <strong class="fs-4">Percentual de Acertos</strong>
                <img src="/img/dashboard_${userId}.png?v=${Date.now()}" alt="Performance Image" style="width: 180px; height: 150px;" />
                <div class="mt-2">
                  <div class="text-success ${acertosStyle}">${percentualAcertos}% Acertos</div>
                  <div class="text-danger ${errosStyle}">${percentualErros}% Erros</div>
                </div>
              </div>

              <div class="d-flex flex-column align-items-center" style="width: 200px;">
                <strong class="fs-5">Melhor Time</strong>
                <img src="${melhorTime.imageTeam}" alt="${melhorTime.teamName}" style="width: 60px; height: 60px" />
                <div class="d-flex flex-row justify-content-center align-items-center gap-2">
                  <div class="text-success fw-bold fs-6">Greens</div>
                  <div class="text-white fw-semibold fs-6">${melhorTime.totalGreens}</div>
                </div>

                <strong class="fs-5 mt-3">Pior Time</strong>
                <img src="${piorTime.imageTeam}" alt="${piorTime.teamName}" style="width: 60px; height: 60px" />
                <div class="d-flex flex-row justify-content-center align-items-center gap-2">
                  <div class="text-danger fw-bold fs-6">Reds</div>
                  <div class="text-white fw-semibold fs-6">${piorTime.totalReds}</div>
                </div>
              </div>
            </div>
          `;

          statsContainer.innerHTML = html;
        })
        .catch(error => {
          console.error('Erro ao gerar o dashboard:', error);
          showNoData();
        });
      })
      .catch(error => {
        console.error('Erro ao buscar performance:', error);
        showNoData();
      });
  })
  .catch(error => {
    console.error('Erro ao buscar dados de performance:', error);
    showNoData();
  });
