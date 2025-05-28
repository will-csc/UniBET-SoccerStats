import sys
import mysql.connector
import matplotlib.pyplot as plt
import os

def conectar_banco():
    return mysql.connector.connect(
        host="localhost",
        user="root",
        password="Sny-301mv",
        database="futstats"
    )

def buscar_apostas_usuario(user_id):
    conn = conectar_banco()
    cursor = conn.cursor()

    query = """
    SELECT 
        g.id_guess, 
        t.team_name, 
        g.bet_amount,
        g.profit_loss
    FROM guesses g
    JOIN teams t ON g.myteam_id = t.id_team
    WHERE g.id_user = %s
    ORDER BY g.id_guess;
    """
    cursor.execute(query, (user_id,))
    apostas = cursor.fetchall()
    cursor.close()
    conn.close()
    return apostas

def gerar_grafico(apostas, user_id):
    if not apostas:
        print(f"Nenhuma aposta encontrada para o usuário ID {user_id}.")
        sys.exit(1)  # Retorna erro para o Java

    nomes = [f"{guess_id} - {team}" for guess_id, team, _, _ in apostas]
    valores = [float(pl) if pl is not None else 0.0 for _, _, _, pl in apostas]

    plt.figure(figsize=(12, 6))
    cores = ['green' if v >= 0 else 'red' for v in valores]
    barras = plt.bar(nomes, valores, color=cores)

    plt.title(f"Lucro/Prejuízo por Aposta (Usuário ID: {user_id})")
    plt.xlabel("ID da Aposta - Time Apostado")
    plt.ylabel("Lucro / Prejuízo (R$)")
    plt.xticks(rotation=45, ha='right')
    plt.grid(axis='y', linestyle='--', alpha=0.7)

    for bar in barras:
        height = bar.get_height()
        plt.text(bar.get_x() + bar.get_width()/2.0, height, f'{height:.2f}', ha='center', va='bottom' if height >= 0 else 'top')

    # Caminho para salvar imagem
    base_dir = os.path.abspath(os.path.join(
        os.path.dirname(__file__),
        '..', '..', '..', '..',
        'resources', 'static', 'img'
    ))
    os.makedirs(base_dir, exist_ok=True)
    nome_arquivo = f'performance_{user_id}.png'
    caminho_imagem = os.path.join(base_dir, nome_arquivo)

    plt.tight_layout()
    plt.savefig(caminho_imagem)
    print(f"Imagem salva em: {caminho_imagem}")

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Uso: python grafico_apostas.py <id_user>")
        sys.exit(1)

    try:
        id_user = int(sys.argv[1])
    except ValueError:
        print("Erro: <id_user> deve ser um número inteiro.")
        sys.exit(1)

    apostas = buscar_apostas_usuario(id_user)
    gerar_grafico(apostas, id_user)
