// ===================== Função para Inserir as Apostas ===========================
function insertBets(bets) {
    const tbody = document.getElementById("tableGuess");
    tbody.innerHTML = ""; // limpa a tabela

    bets.forEach(bet => {
        const row = document.createElement("tr");

        row.innerHTML = `
            <td>${bet.winner}</td>
            <td>${bet.odd}</td>
            <td>R$ ${parseFloat(bet.value).toFixed(2)}</td>
            <td>${bet.gameDate}</td>
            <td>${bet.probableWinner}</td>
            <td>${bet.winProbability}</td>
            <td>${bet.suggestedValue}</td>
            <td>${bet.unpredictability}</td>
            <td>
                <button class="btn btn-success btn-sm me-1" onclick="updateBet(${bet.guessId},1)">Acerto</button>
                <button class="btn btn-danger btn-sm" onclick="updateBet(${bet.guessId},0)">Erro</button>
                <button class="btn btn-outline-secondary btn-sm" onclick="deleteBet(${bet.guessId})">🗑️</button>
            </td>
        `;

        tbody.appendChild(row);
    });
}

// Quando o DOM estiver pronto, insere os times
document.addEventListener("DOMContentLoaded", () => {
    fetch("/api/bets")
        .then(res => res.json())
        .then(bets => {
            console.log(bets);
            insertBets(bets);
        })
        .catch(error => {
            console.error("Erro ao buscar dados das apostas:", error);
        });
});

function deleteBet(id) {
    console.log("ID para deletar:", id);
    fetch(`api/delete-bet/${id}`, {
        method: 'DELETE'
    })
    .then(response => {
        if (response.ok) {
            alert("Aposta deletada com sucesso.");
            location.reload();
        } else {
            return response.text().then(text => { throw new Error(text); });
        }
    })
    .catch(error => {
        console.error("Erro ao deletar aposta:", error);
        alert("Erro ao deletar a aposta.");
    });
}

function updateBet(guessId, option) {
  fetch(`http://localhost:8080/api/update-bet/${guessId}?option=${option}`, {
    method: 'PUT'
  })
  .then(response => {
    if (!response.ok) {
      throw new Error('Erro ao atualizar aposta');
    }
    alert('Aposta atualizada com sucesso!');
    location.reload();
    // Atualize a UI aqui se quiser
  })
  .catch(err => {
    console.error('Erro ao atualizar aposta:', err);
    alert('Falha ao atualizar aposta.');
  });
}

// ===================== Função para Inserir os Select's dos Jogos e Times ===========================
function insertSelects(games) {
    const selectGamesDiv = document.getElementById("selectGames");
    const selectTeamsDiv = document.getElementById("selectTeams");

    // Remove selects anteriores
    const existingGameSelect = selectGamesDiv.querySelector("select");
    if (existingGameSelect) existingGameSelect.remove();

    const existingTeamSelect = selectTeamsDiv.querySelector("select");
    if (existingTeamSelect) existingTeamSelect.remove();

    // Cria <select> de jogos
    const gameSelect = document.createElement("select");
    gameSelect.className = "form-control fs-6 mx-auto";
    gameSelect.id = "gameInput";
    gameSelect.name = "game";
    gameSelect.style.width = "58%";
    gameSelect.style.paddingTop = "0px";
    gameSelect.style.paddingBottom = "0px";

    gameSelect.innerHTML = games.map((game, index) => {
        const label = `${game.homeTeam} x ${game.awayTeam} - ${game.gameDate}`;
        return `<option value="${game.homeTeam} x ${game.awayTeam} - ${game.gameDate}">${label}</option>`;
    }).join("");
    selectGamesDiv.appendChild(gameSelect);

    // Função para criar o select de times
    function updateTeamSelect(index) {
        const game = games[index];
        const teamSelect = document.createElement("select");
        teamSelect.id = "winnerInput";
        teamSelect.className = "form-control fs-6 mx-auto";
        teamSelect.name = "winner_id";
        teamSelect.style.width = "25%";
        teamSelect.style.paddingTop = "0px";
        teamSelect.style.paddingBottom = "0px";

        teamSelect.innerHTML = `
            <option value="${game.homeTeam}">${game.homeTeam}</option>
            <option value="${game.awayTeam}">${game.awayTeam}</option>
        `;

        const oldTeamSelect = selectTeamsDiv.querySelector("select");
        if (oldTeamSelect) oldTeamSelect.remove();

        selectTeamsDiv.appendChild(teamSelect);
    }

    // Inicializa com o primeiro jogo
    updateTeamSelect(0);

    // Atualiza os times quando mudar o jogo
    gameSelect.addEventListener("change", (e) => {
        const selectedIndex = e.target.selectedIndex;
        updateTeamSelect(selectedIndex);
    });

}

// Quando o DOM estiver pronto, insere os times
document.addEventListener("DOMContentLoaded", () => {
    Promise.all([
        fetch("/api/games").then(res => res.json())
    ])
    .then(([teams, games]) => {
        insertSelects(teams, games);
    })
    .catch(error => {
        console.error("Erro ao buscar dados das APIs:", error);
    });
});

// ===================== Função para Validar o Forms ===========================
function validateForms() {
    const gameSelect = document.querySelector("#selectGames select");
    const teamSelect = document.querySelector("#selectTeams select");
    const moneyInput = document.getElementById("moneyInput");
    const oddInput = document.getElementById("oddInput");

    let valid = true;
    let messages = [];

    // Valida jogo
    if (!gameSelect || gameSelect.value.trim() === "") {
        valid = false;
        messages.push("Selecione um jogo.");
    }

    // Valida time
    if (!teamSelect || teamSelect.value.trim() === "") {
        valid = false;
        messages.push("Selecione um time vencedor.");
    }

    // Valida valor apostado
    const moneyValue = parseFloat(mask.unmaskedValue || "0");
    if (isNaN(moneyValue) || moneyValue < 0.25) {
        valid = false;
        messages.push("Insira um valor válido (mínimo R$ 0,25).");
    }

    // Valida odd
    const oddValue = parseFloat(oddInput.value);
    if (isNaN(oddValue) || oddValue < 0.01) {
        valid = false;
        messages.push("Insira uma odd válida (mínimo 0.01).");
    }

    // Mostra mensagens de erro
    if (!valid) {
        alert(messages.join("\n"));
    }

    return valid;
}

// ===================== Função para Salvar as Apostas ===========================
document.getElementById('guessForm').addEventListener('submit', function(event) {
    event.preventDefault(); // impede o submit padrão

    // aqui você pode colocar sua validação (antes você usava validateForms())
    // se quiser manter, faça algo como:
    // if (!validateForms()) return;

    const game = document.getElementById('gameInput').value;
    const winner_id = document.getElementById('winnerInput').value;
    const betAmount = document.getElementById('moneyInput').value;
    const odd = document.getElementById('oddInput').value;

    const data = new URLSearchParams();
    data.append('game', game);
    data.append('winner_id', winner_id);
    data.append('betAmount', betAmount);
    data.append('odd', odd);

    fetch('/new-guess', {
        method: 'POST',
        body: data,
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    })
    .then(async response => {
        const text = await response.text();
        if (response.ok) {
            alert(text); // mensagem do backend
            document.getElementById('guessForm').reset();
            location.reload();
        } else {
            alert('Erro: ' + text);
        }
    })
    .catch(error => {
        console.error('Erro na requisição:', error);
        alert('Erro ao salvar a aposta.');
    });
});

