import plotly.graph_objects as go
import sys
import os

def make_dashboard(acertos, erros, id):
    opcoes = ["Acertos", "Erros"]
    valores = [acertos, erros]
    cores = ["Green", "Red"]

    fig = go.Figure(data=go.Pie(
        labels=opcoes,
        values=valores,
        marker_colors=cores
    ))

    # Caminho relativo ao projeto
    base_dir = os.path.abspath(os.path.join(
        os.path.dirname(__file__),
        '..', '..', '..', '..',  # sobe 4 níveis
        'resources', 'static', 'img'
    ))

    caminho_imagem = os.path.join(base_dir, f'dashboard_{id}.png')

    fig.write_image(caminho_imagem)
    print(f"Imagem salva em: {caminho_imagem}")

# Argumentos recebidos
acertos = int(sys.argv[1])
erros = int(sys.argv[2])
id = int(sys.argv[3])

make_dashboard(acertos, erros, id)
