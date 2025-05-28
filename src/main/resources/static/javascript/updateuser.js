// ============= Atualizar o Usuário ==============
async function updateUser(userId) {
    const usernameInput = document.getElementById("usernameInput");
    const emailInput = document.getElementById("emailInput");
    const passwordInput = document.getElementById("passwordInput");
    const fileInput = document.getElementById("fileInput");

    const username = usernameInput ? usernameInput.value.trim() : "";
    const email = emailInput ? emailInput.value.trim() : "";
    const password = passwordInput ? passwordInput.value.trim() : "";
    const imageFile = fileInput && fileInput.files.length > 0 ? fileInput.files[0] : null;

    // Validações simples
    if(!username && !email && !password && !imageFile){
        alert("Não há dados para serem atualizados.");
        return;
    }

    if (username.length > 50) {
        alert("O nome de usuário deve ter no máximo 50 caracteres.");
        return;
    }

    if (email.length > 50) {
        alert("O email deve ter no máximo 100 caracteres.");
        return;
    }
    // Validação simples de email usando regex
    if (password.length > 12) {
        alert("A senha deve ter no máximo 255 caracteres e minímo 12.");
        return;
    }

    // Se tiver imagem, valida tipo e tamanho (ex: até 5MB)
    if (imageFile) {
        const allowedTypes = ["image/jpeg", "image/png", "image/gif"];
        if (!allowedTypes.includes(imageFile.type)) {
            alert("Formato de imagem inválido. Use JPEG, PNG ou GIF.");
            return;
        }
        const maxSizeMB = 5;
        if (imageFile.size > maxSizeMB * 1024 * 1024) {
            alert(`Imagem muito grande. O tamanho máximo é ${maxSizeMB} MB.`);
            return;
        }
    }

    // Se passou nas validações, envia o formulário
    const formData = new FormData();
    formData.append("username", username);
    formData.append("email", email);
    formData.append("userPassword", password);
    if (imageFile) {
        formData.append("userImage", imageFile);
    }

    try {
        const response = await fetch(`/api/users/${userId}`, {
            method: "PUT",
            body: formData
        });

        if (response.ok) {
            alert("Usuário atualizado com sucesso!");
            window.location.reload();
        } else {
            const error = await response.text();
            alert("Erro ao atualizar: " + error);
        }
    } catch (err) {
        alert("Erro de rede: " + err.message);
    }
}

// ============= Encaminhar o código ==============
function sendRankCode(userId) {
        fetch(`/api/send-code/${userId}`, {
            method: 'POST'
        })
        .then(response => {
            if (!response.ok) {
                throw new Error("Erro ao enviar o código.");
            }
            return response.text();
        })
        .then(data => {
            alert("Código enviado com sucesso!");
        })
        .catch(error => {
            alert("Erro ao enviar o código.");
        });
    }

// ============= Deletar o Usuário ==============
function deleteUser(userId) {
        fetch(`/api/users/${userId}`, {
            method: 'DELETE'
        })
        .then(response => {
            if (!response.ok) {
                throw new Error("Erro ao excluir o usuário.");
            }
            return response.text();
        })
        .then(data => {
            alert("Usuário excluído com sucesso.");
            window.location.href = "/logout";
        })
        .catch(error => {
            console.error(error);
            alert("Erro ao excluir o usuário.");
        });
    }

// ============= Enviar codigo ==============
const userIdElement = document.getElementById("userId");
const userId = userIdElement ? userIdElement.value : null;
const input = document.getElementById('rankCodeInput');

input.addEventListener('input', () => {
    const val = input.value.trim();
    if (val.length === 12) {
      input.disabled = true;
      sendCode(val);
    }
  });

input.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      const val = input.value.trim();
      if (val.length === 12) {
        input.disabled = true;
        sendCode(val);
      } else {
        alert('Por favor, digite exatamente 12 dígitos antes de enviar.');
      }
    }
  });

function sendCode(codigo) {
  const userIdElement = document.getElementById("userId");
  const userId = userIdElement ? userIdElement.value : null;

  if (!userId) {
    alert("ID do usuário não encontrado.");
    return;
  }

  fetch(`/api/validate-rank/${userId}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ rankCode: codigo })
  })
  .then(res => {
    if (!res.ok) throw new Error('Código errado!');
    location.reload();
    return res.text();
  })
  .then(msg => {
    alert(msg);
    location.reload();
  })
  .catch(err => {
    alert(err.message);
    input.disabled = false;
  });
}

// ============= Mudar conteúdo da Div ==============
document.addEventListener("DOMContentLoaded", () => {
    const rankElement = document.getElementById("userRank");
    const userRank = rankElement ? parseInt(rankElement.value) : 1;

    if (userRank > 1) {
        const statsDiv = document.getElementById("stats-code");

        statsDiv.innerHTML = `
            <div style="display: grid; grid-template-columns: 1fr 1fr; grid-gap: 20px; width: 100%; padding: 20px;">

                <!-- Melhores Apostas -->
                <div style="width: 100%; background-color: #0e0a24; border-radius: 10px; padding: 15px;">
                    <h3 style="color: white;">Melhores Apostas</h3>
                    <div id="best-bets" style="height: 150px; background-color: #FFFFFF; border-radius: 8px;"></div>
                    <h5 id="profit" style="color: green; font-weight: bold; margin-top: 10px;"></h5>
                </div>

                <!-- Piores Apostas -->
                <div style="background-color: #0e0a24; border-radius: 10px; padding: 15px;">
                    <h3 style="color: white;">Piores Apostas</h3>
                    <div id="worst-bets" style="height: 150px; background-color: #FFFFFF; border-radius: 8px;"></div>
                    <h5 id="loss" style="color: red; font-weight: bold; margin-top: 10px;"></h5>
                </div>

                <!-- Evolução do Usuário -->
                <div style="background-color: #0e0a24; border-radius: 10px; padding: 15px;">
                    <h3 style="color: white;">Evolução do Usuário</h3>
                    <div id="evolution" style="height: 150px; background-color: #FFFFFF; border-radius: 8px;"></div>
                </div>

                <!-- Percentual de Acertos -->
                <div style="background-color: #0e0a24; border-radius: 10px; padding: 15px;">
                    <h3 style="color: white;">Percentual de Acertos</h3>
                    <div id="rightanswers" style="height: 150px; background-color: #FFFFFF; border-radius: 8px;"></div>
                </div>

                <!-- Principais times -->
                <div style="background-color: #0e0a24; border-radius: 10px; padding: 15px;">
                    <h3 style="color: white;">Principal time</h3>
                    <div id="best-teams" style="display: flex; gap: 10px; margin-top: 10px; background-color: #FFFFFF"></div>
                </div>

                <!-- Piores times -->
                <div style="background-color: #0e0a24; border-radius: 10px; padding: 15px;">
                    <h3 style="color: white;">Pior time</h3>
                    <div id="worst-teams" style="display: flex; gap: 10px; margin-top: 10px; background-color: #FFFFFF"></div>
                </div>

                <!-- Modal (inicialmente invisível) -->
                <div id="imgModal" class="modal" onclick="closeModal()">
                    <span class="close">&times;</span>
                    <img class="modal-content" id="modalImage">
                </div>

            </div>
        `;
    }
    bestAndWorstGuesses();
    rightGuesses();
    bestAndWorstTeams();
    userEvolution();
});

function bestAndWorstGuesses() {
  fetch("/api/top-bets")
    .then(response => response.json())
    .then(bets => {
      const bestContainer = document.getElementById("best-bets");
      const worstContainer = document.getElementById("worst-bets");
      const totalProfitElement = document.getElementById("profit");
      const totalLossElement = document.getElementById("loss");

      if (!bets.length) {
        bestContainer.innerHTML = `
          <div class="text-center mt-4">
            <img src="/img/no-data.jpg" alt="Sem dados" style="width: 150px;height: 150px">
            
          </div>
        `;
        worstContainer.innerHTML = bestContainer.innerHTML;
        totalProfitElement.textContent = "Lucro Total: R$ 0,00";
        totalLossElement.textContent = "Prejuízo Total: R$ 0,00";
        return;
      }

      // Calcula lucro líquido de cada aposta
      bets.forEach(bet => {
        bet.profit = (bet.odd * bet.value) - bet.value;
      });

      // Encontra o melhor e pior lucro
      const wonBets = bets.filter(bet => bet.myTeamId === bet.winnerId && bet.profit > 0);
      const bestBet = wonBets.length
        ? wonBets.reduce((prev, curr) => (curr.profit > prev.profit ? curr : prev))
        : null;
      // encontra a aposta com maior prejuízo onde o time escolhido NÃO venceu
      const lostBets = bets.filter(bet => bet.myTeamId !== bet.winnerId && bet.profit < 0);
      const worstBet = lostBets.length
        ? lostBets.reduce((prev, curr) => (curr.profit < prev.profit ? curr : prev))
        : null;


     // Total de lucros e prejuízos
      const totalProfit = bets
        .filter(b => b.profit > 0)
        .reduce((acc, b) => acc + b.profit, 0);

      const totalLoss = bets
        .filter(b => b.profit < 0)
        .reduce((acc, b) => acc + b.profit, 0); // negativo

      // Atualiza os campos de total
      totalProfitElement.textContent = `Lucro Total: R$ ${totalProfit.toFixed(2)}`;
      totalLossElement.textContent = `Prejuízo Total: R$ ${Math.abs(totalLoss).toFixed(2)}`;

      if (bestBet) {bestContainer.innerHTML = `
          <div class="d-flex align-items-center" style="flex-shrink: 0;">
              <img src="${bestBet.winnerImage}" alt="Time" style="width: 50px; height: 50px;">
            </div>
            <div class="d-flex align-items-center flex-grow-1 justify-content-center px-3">
              <strong class="fs-6 m-0 text-wrap text-center">${bestBet.myTeam} venceu ${bestBet.otherTeam}</strong>
            </div>
            <div class="text-success text-center mt-2 fw-bold">
              Lucro: R$ ${bestBet.profit.toFixed(2)}
            </div>
          `;
      }else {
        bestContainer.innerHTML = `
          <div style="height: 100%; display: flex; align-items: center; justify-content: center;">
            <strong class="fs-6 m-0 text-wrap text-center">Você não acertou nenhuma aposta ainda</strong>
          </div>
        `;
      }

      if (worstBet) {worstContainer.innerHTML = `
           <div class="d-flex align-items-center" style="flex-shrink: 0;">
              <img src="${worstBet.winnerImage}" alt="Time" style="width: 50px; height: 50px;">
            </div>
            <div class="d-flex align-items-center flex-grow-1 justify-content-center px-3">
              <strong class="fs-6 m-0 text-wrap text-center">${worstBet.myTeam} perdeu para ${worstBet.otherTeam}</strong>
            </div>
            <div class="text-danger text-center mt-2 fw-bold">
              Prejuízo: R$ ${Math.abs(worstBet.profit).toFixed(2)}
            </div>
          `;
      }else {
         worstContainer.innerHTML = `
           <div style="height: 100%; display: flex; align-items: center; justify-content: center;">
             <strong class="fs-6 m-0 text-wrap text-center">Você não errou nenhuma aposta ainda</strong>
           </div>
         `;
      }
    })
    .catch(err => {
      console.error("Erro ao buscar apostas:", err);
    });
}

function bestAndWorstTeams(){
    fetch('/api/top-winners')
      .then(response => {
        if (!response.ok) throw new Error("Erro ao buscar times");
        return response.json();
      })
      .then(teams => {
        if (!Array.isArray(teams) || teams.length === 0) return;

        // Melhor time (mais acertos)
        const melhorTime = teams.reduce((prev, curr) => {
          return (curr.totalGreens > prev.totalGreens) ? curr : prev;
        });

        // Pior time (mais erros)
        const piorTime = teams.reduce((prev, curr) => {
          return (curr.totalReds > prev.totalReds) ? curr : prev;
        });

        // Criar HTML do melhor time
        const bestHTML = `
          <div class="text-center p-2 flex-column" style="width: 100%">
            <img src="${melhorTime.imageTeam}" alt="${melhorTime.teamName}" style="width: 80px;" />
            <p class="fw-bold mt-1 mb-0">${melhorTime.teamName}</p>
            <p class="text-success mb-0">${melhorTime.totalGreens} acertos</p>
          </div>
        `;

        // Criar HTML do pior time
        const worstHTML = `
          <div class="text-center p-2 flex-column" style="width: 100%">
            <img src="${piorTime.imageTeam}" alt="${piorTime.teamName}" style="width: 80px;" />
            <p class="fw-bold mt-1 mb-0">${piorTime.teamName}</p>
            <p class="text-danger mb-0">${piorTime.totalReds} erros</p>
          </div>
        `;

        // Atualizar os elementos no DOM
        const bestDiv = document.getElementById('best-teams');
        const worstDiv = document.getElementById('worst-teams');

        if (bestDiv) bestDiv.innerHTML = bestHTML;
        if (worstDiv) worstDiv.innerHTML = worstHTML;
      })
      .catch(error => {
        console.error("Erro ao processar os dados dos times:", error);
      });
}

function rightGuesses() {
  fetch('/api/performancestats')
    .then(response => {
      if (!response.ok) {
        document.getElementById('rightanswers').innerHTML = `
                <div class="text-center mt-4">
                  <img src="/img/no-data.jpg" alt="Sem dados" style="width: 150px;height: 150px">
                  
                </div>
              `;
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

      // Aqui você pode usar os estilos se quiser (acertosStyle, errosStyle), mas não está usando no HTML atual

      fetch(`/api/generate-dashboard?acertos=${totalAcertos}&erros=${totalErros}`, {
        method: 'POST'
      })
        .then(response => {
          if (!response.ok) throw new Error('Erro ao gerar o dashboard');

          const timestamp = new Date().getTime();
          const imgSrc = `/img/dashboard.png?v=${timestamp}`;

          const html = `
            <div class="d-flex flex-column align-items-center" style="width: 100%;">
              <img src="${imgSrc}" alt="Performance Image"
                   style="width: 180px; height: 150px; cursor: zoom-in;"
                   onclick="openModal('${imgSrc}')"/>
            </div>
          `;

          document.getElementById('rightanswers').innerHTML = html;
        })
        .catch(error => {
          console.error('Erro ao gerar o dashboard:', error);
        });
    })
    .catch(error => {
      console.error('Erro ao obter performance:', error);
    });
}

function userEvolution() {
    fetch("/make-dashboard-performance", { method: "POST" })
    .then(response => {
        if (!response.ok){
            document.getElementById('evolution').innerHTML = `
                <div class="text-center mt-4">
                    <img src="/img/no-data.jpg" alt="Sem dados" style="width: 150px;height: 150px">
                </div>`;
            return null;
        }
        return response.text(); // agora retornará o nome da imagem
    })
    .then(fileName => {
        if (!fileName) return;
        const div = document.getElementById("evolution");
        const timestamp = new Date().getTime();
        const imgSrc = `/img/${fileName}?${timestamp}`;

        div.innerHTML = `
            <img src="${imgSrc}"
                alt="Gráfico de Performance"
                style="width: 100%; height: 140px; object-fit: contain; cursor: zoom-in;"
                onclick="openModal('${imgSrc}')" />
        `;
    })
    .catch(error => {
        console.error("Erro:", error);
        document.getElementById("evolution").innerHTML = "<p style='color:red;'>Erro ao carregar gráfico.</p>";
    });
}

function openModal(src) {
    document.getElementById("modalImage").src = src;
    document.getElementById("imgModal").style.display = "block";
}

function closeModal() {
    document.getElementById("imgModal").style.display = "none";
}


