// ===================== Função para Inserir novo Time ===========================
function insertEmptyRow() {
    const tbody = document.getElementById("tableTeams");

    const row = document.createElement("tr");

    row.innerHTML = `
        <td><input type="text" class="form-control form-control-sm" maxlength="20" value=""></td>
        <td><input type="file" class="form-control form-control-sm" accept="image/*"></td>
        <td>
            <select class="form-select form-select-sm">
                <option value="OK">OK</option>
                <option value="BOA">BOA</option>
                <option value="RUIM">RUIM</option>
            </select>
        </td>
        <td><input type="number" class="form-control form-control-sm" min="1" max="20" value=""></td>
        <td><input type="number" class="form-control form-control-sm" min="1" max="5" value=""></td>
        <td>
            <select class="form-select form-select-sm">
                <option value="OK">OK</option>
                <option value="BOA">BOA</option>
                <option value="RUIM">RUIM</option>
            </select>
        </td>
        <td>
            <button class="btn btn-success btn-sm" onclick="saveTeam(this)">Salvar</button>
            <button class="btn btn-danger btn-sm" onclick="deleteRow(this)">Excluir</button>
        </td>
    `;

    tbody.insertBefore(row, tbody.firstChild);
}

// ===================== Função para Inserir os Times ===========================
function insertTeams(teams) {
    const tbody = document.getElementById("tableTeams");
    tbody.innerHTML = ""; // limpa qualquer conteúdo antigo

    if (!teams || teams.length === 0) {
        insertEmptyRow();
        return;
    }

    teams.forEach(team => {
        const row = document.createElement("tr");

        row.innerHTML = `
            <td><input type="text" class="form-control form-control-sm" maxlength="20" value="${team.teamName}"></td>
            <td><input type="file" class="form-control form-control-sm" accept="image/*"></td>
            <td>
                <select class="form-select form-select-sm">
                    <option value="OK" ${team.teamFeeling === 'OK' ? 'selected' : ''}>OK</option>
                    <option value="BOA" ${team.teamFeeling === 'BOA' ? 'selected' : ''}>BOA</option>
                    <option value="RUIM" ${team.teamFeeling === 'RUIM' ? 'selected' : ''}>RUIM</option>
                </select>
            </td>
            <td><input type="number" class="form-control form-control-sm" min="1" max="20" value="${team.tableStading}"></td>
            <td><input type="number" class="form-control form-control-sm" min="1" max="5" value="${team.totalCompetitions}"></td>
            <td>
                <select class="form-select form-select-sm">
                    <option value="OK" ${team.motivation === 'OK' ? 'selected' : ''}>OK</option>
                    <option value="BOA" ${team.motivation === 'BOA' ? 'selected' : ''}>BOA</option>
                    <option value="RUIM" ${team.motivation === 'RUIM' ? 'selected' : ''}>RUIM</option>
                </select>
            </td>
            <td>
                <button class="btn btn-success btn-sm" onclick="saveTeam(this)">Salvar</button>
                <button class="btn btn-danger btn-sm" onclick="deleteTeam('${team.teamName}', this)">Excluir</button>
            </td>
        `;

        tbody.appendChild(row);
    });
}

// Quando o DOM estiver pronto, insere os times
document.addEventListener("DOMContentLoaded", () => {
    fetch("/api/teams")
        .then(response => response.json())
        .then(teams => {
            insertTeams(teams);
        })
        .catch(error => {
            console.error("Erro ao buscar times:", error);
            insertEmptyRow(); // insere linha vazia se a API falhar
        });
});

// ===================== Função para Salvar os Times ===========================
function saveTeam(button) {
    const row = button.closest("tr");
    const inputs = row.querySelectorAll("input, select");

    const teamName = inputs[0].value;
    if (teamName.length > 20) {
        alert("O nome do time não pode ter mais de 20 caracteres.");
        return;
    }

    const fileInput = inputs[1];
    const file = fileInput.files[0];

    const formData = new FormData();
    formData.append("team_name", inputs[0].value);
    formData.append("team_image", file); // nome deve ser exatamente "team_image"
    formData.append("team_feeling", inputs[2].value);
    formData.append("table_standing", inputs[3].value);
    formData.append("total_competitions", inputs[4].value);
    formData.append("motivation", inputs[5].value);

    fetch("/api/team", {
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
                alert("Time atualizado com sucesso!");
            } else {
                alert("Time salvo com sucesso!");
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

// ===================== Função para Deletar os Times ===========================
function deleteRow(button) {
    const row = button.closest("tr");
    row.remove();
}

function deleteTeam(nome, button) {
    if (!confirm("Tem certeza que deseja excluir este time?")) return;

    fetch(`/api/team/${encodeURIComponent(nome)}`, {
        method: "DELETE"
    })
    .then(response => response.text().then(msg => {
        if (response.ok) {
            alert(msg);
            button.closest("tr").remove(); // remove linha da tabela
        } else {
            alert("Erro: " + msg);
        }
    }))
    .catch(error => {
        console.error("Erro ao excluir time:", error);
        alert("Erro na comunicação com o servidor.");
    });
}

