// ===================== Função para Inserir novo Time ===========================
function insertEmptyRow() {
    const tbody = document.getElementById("gamesTable");

    fetch("/api/teams")
        .then(response => response.json())
        .then(teams => {
            const teamOptions = teams.map(team =>
                `<option value="${team.teamName}">${team.teamName}</option>`
            ).join("");

            const row = document.createElement("tr");

            row.innerHTML = `
                <!-- Dia -->
                <td>
                    <input type="date" class="form-control form-control-sm">
                </td>
                <!-- Nome e Gols do Mandante -->
                <td>
                    <select class="form-select form-select-sm">
                        ${teamOptions}
                    </select>
                </td>
                <td><input type="number" min="0" class="form-control form-control-sm" value="0"></td>

                <!-- Nome e Gols do Visitante -->
                <td>
                    <select class="form-select form-select-sm">
                        ${teamOptions}
                    </select>
                </td>
                <td><input type="number" min="0" class="form-control form-control-sm" value="0"></td>

                <!-- Escanteios -->
                <td><input type="number" min="0" class="form-control form-control-sm" value="0"></td>
                <td><input type="number" min="0" class="form-control form-control-sm" value="0"></td>

                <!-- Chutes -->
                <td><input type="number" min="0" class="form-control form-control-sm" value="0"></td>
                <td><input type="number" min="0" class="form-control form-control-sm" value="0"></td>

                <!-- Importância -->
                <td>
                    <select class="form-select form-select-sm">
                        <option value="ALTA">Alta</option>
                        <option value="REGULAR">Regular</option>
                        <option value="BAIXA">Baixa</option>
                    </select>
                </td>

                <!-- Botões -->
                <td>
                    <button class="btn btn-success btn-sm" onclick="saveGame(this)">Salvar</button>
                    <button class="btn btn-danger btn-sm" onclick="deleteRow(this)">Excluir</button>
                </td>
            `;

            tbody.insertBefore(row, tbody.firstChild);
        })
        .catch(error => {
            console.error("Erro ao buscar times para linha vazia:", error);
        });
}

// ===================== Função para Inserir os Jogos ===========================
function insertGames(teams, games) {
    const tbody = document.getElementById("gamesTable");
    tbody.innerHTML = ""; // limpa qualquer conteúdo antigo

    if (!teams || teams.length === 0 || !games || games.length === 0) {
        insertEmptyRow();
        return;
    }

    // Gera as opções de times para os selects
    const teamOptions = (selectedTeam) =>
        teams.map(team =>
            `<option value="${team.teamName}" ${team.teamName === selectedTeam ? 'selected' : ''}>${team.teamName}</option>`
        ).join("");

    games.forEach(game => {
        const row = document.createElement("tr");

        row.innerHTML = `
            <td>
                <input type="date" class="form-control form-control-sm" value="${game.gameDate}">
            </td>
            <td>
                <select class="form-select form-select-sm">
                    ${teamOptions(game.homeTeam)}
                </select>
            </td>
            <td><input type="number" min="0" class="form-control form-control-sm" value="${game.homeGoals}"></td>

            <td>
                <select class="form-select form-select-sm">
                    ${teamOptions(game.awayTeam)}
                </select>
            </td>
            <td><input type="number" min="0" class="form-control form-control-sm" value="${game.awayGoals}"></td>

            <td><input type="number" min="0" class="form-control form-control-sm" value="${game.homeCorners}"></td>
            <td><input type="number" min="0" class="form-control form-control-sm" value="${game.awayCorners}"></td>

            <td><input type="number" min="0" class="form-control form-control-sm" value="${game.homeShots}"></td>
            <td><input type="number" min="0" class="form-control form-control-sm" value="${game.awayShots}"></td>

            <td>
                <select class="form-select form-select-sm">
                    <option value="ALTA" ${game.importance === 'ALTA' ? 'selected' : ''}>ALTA</option>
                    <option value="REGULAR" ${game.importance === 'REGULAR' ? 'selected' : ''}>REGULAR</option>
                    <option value="BAIXA" ${game.importance === 'BAIXA' ? 'selected' : ''}>BAIXA</option>
                </select>
            </td>

            <td>
                <button class="btn btn-success btn-sm" onclick="saveGame(this)">Salvar</button>
                <button class="btn btn-danger btn-sm" onclick="deleteGame(this)">Excluir</button>
            </td>
        `;

        tbody.appendChild(row);
    });
}

// Quando o DOM estiver pronto, insere os times
document.addEventListener("DOMContentLoaded", () => {
    Promise.all([
        fetch("/api/teams").then(res => res.json()),
        fetch("/api/games").then(res => res.json())
    ])
    .then(([teams, games]) => {
        insertGames(teams, games);
    })
    .catch(error => {
        console.error("Erro ao buscar dados:", error);
        insertEmptyRow();
    });
});

// ===================== Função para Salvar os Times ===========================
function saveGame(button) {
    const row = button.closest("tr");
    const inputs = row.querySelectorAll("input, select");

    // Tenta pegar o input de data dentro da linha
    const gameDateInput = row.querySelector("input[type='date']");
    const gameDate = gameDateInput.value;

    const selects = row.querySelectorAll("select");
    const inputsNumber = row.querySelectorAll("input[type='number']");

    const homeTeam = selects[0].value;
    const homeGoals = inputsNumber[0].value;
    const awayTeam = selects[1].value;
    const awayGoals = inputsNumber[1].value;
    const homeCorners = inputsNumber[2].value;
    const awayCorners = inputsNumber[3].value;
    const homeShots = inputsNumber[4].value;
    const awayShots = inputsNumber[5].value;
    const gameImportance = selects[2].value

    // Validações
    if (!isValidDate(gameDate)) {
        alert("Data inválida ou inexistente.");
        return;
    }

    if (!isPositive(homeGoals, awayGoals, homeCorners, awayCorners, homeShots, awayShots)) {
        alert("Todos os valores numéricos devem ser maiores, ou iguais ao zero.");
        return;
    }

    // Prepara o formData para enviar
    const formData = new FormData();
    formData.append("game_date", gameDate);
    formData.append("home_team", homeTeam);
    formData.append("home_goals", homeGoals);
    formData.append("away_team", awayTeam);
    formData.append("away_goals", awayGoals);
    formData.append("home_corners", homeCorners);
    formData.append("away_corners", awayCorners);
    formData.append("home_shots", homeShots);
    formData.append("away_shots", awayShots);
    formData.append("game_importance", gameImportance);

    fetch("/api/game", {
        method: "POST",
        body: formData
    })
    .then(async response => {
        const contentType = response.headers.get("content-type");
        const responseBody = contentType?.includes("application/json")
            ? await response.json()
            : await response.text();

        if (response.ok) {
            if (responseBody === "Partial") {
                alert("Jogo atualizado com sucesso!");
            } else {
                alert("Jogo salvo com sucesso!");
            }
            location.reload();
        } else {
            alert("Erro: " + responseBody);
        }
    })
    .catch(error => {
        console.error("Erro na requisição:", error);
        alert("Erro ao salvar time.");
    });
}

// Verifica se valores são maiores que 0
function isPositive(...values) {
    return values.every(val => val > -1);
}
// Verifica se a data é válida
function isValidDate(dateString) {
    const date = new Date(dateString);
    return (
        !isNaN(date) &&
        dateString === date.toISOString().split("T")[0]
    );
}

// ===================== Função para Deletar os Times ===========================
function deleteRow(button) {
    const row = button.closest("tr");
    row.remove();
}

function deleteGame(button) {
    // A linha onde o botão está
    const row = button.closest("tr");

    // Pegando a data do input date
    const gameDateInput = row.querySelector("input[type='date']");
    const gameDate = gameDateInput.value; // no formato 'yyyy-mm-dd'

    // Pegando o time mandante do primeiro select (assumindo que o select de homeTeam é o primeiro)
    const homeTeamSelect = row.querySelector("select");
    const homeTeamName = homeTeamSelect.value;

    // Chamada DELETE via fetch
    fetch(`/api/game/${gameDate}/${encodeURIComponent(homeTeamName)}`, {
        method: 'DELETE'
    })
    .then(response => {
        if (response.ok) {
            alert("Jogo deletado com sucesso!");
            row.remove();
        } else {
            return response.text().then(text => { throw new Error(text) });
        }
    })
    .catch(error => alert("Erro ao deletar jogo: " + error.message));
}


