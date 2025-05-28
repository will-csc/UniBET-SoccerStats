const bestTeamsContainer = document.getElementById('best-teams');

fetch('/api/top-winners')
  .then(response => {
    if (!response.ok) {
      throw new Error("Erro ao buscar times");
    }
    return response.json();
  })
  .then(teams => {
      const userRank = parseInt(document.getElementById('userRank').value, 10);
      if (isNaN(userRank) || userRank <= 1) {
        bestTeamsContainer.innerHTML = `
            <div class="text-center mt-4">
                <img src="/img/no-data.jpg" alt="Sem dados" style="width: 150px;height: 150px">
                <p class="fs-5 fw-bold">Não foram encontrados dados.</p>
            </div>
           `;
        return;
      }

      teams.slice(0, 3).forEach(team => {
      const html = `
        <div class="d-flex bg-dark justify-content-between align-items-center rounded p-2 mb-2"
             style="min-width: 460px; height: 90px; background-color: #e9ecef;">

          <!-- Imagem à esquerda -->
          <div class="d-flex align-items-center" style="flex-shrink: 0;">
            <img src="${team.imageTeam}" alt="${team.teamName}" style="width: 80px; height: 80px; border-radius: 8px;">
          </div>

          <!-- Nome centralizado -->
          <div class="d-flex align-items-center flex-grow-1 justify-content-center px-3" style="height: 100%;">
            <strong class="fs-4 text-center m-0">${team.teamName}</strong>
          </div>

          <!-- Greens e Reds à direita -->
          <div class="d-flex align-items-center">
            <div class="text-center me-4">
              <div class="text-success fw-bold fs-5">Greens</div>
              <div class="fw-semibold fs-4">${team.totalGreens ?? 0}</div>
            </div>
            <div class="text-center">
              <div class="text-danger fw-bold fs-5">Reds</div>
              <div class="fw-semibold fs-4">${team.totalReds ?? 0}</div>
            </div>
          </div>
        </div>
      `;
      bestTeamsContainer.insertAdjacentHTML('beforeend', html);
    });
  })
  .catch(error => {
    console.error("Erro ao carregar os dados dos times:", error);
    bestTeamsContainer.innerHTML = "<p class='text-danger'>Erro ao carregar os dados dos times.</p>";
  });
