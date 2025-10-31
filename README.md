# Análise de Desempenho de Algoritmos de Ordenação em Ambientes Paralelos

## Resumo
Este relatório apresenta um estudo experimental sobre o desempenho de quatro algoritmos de ordenação (Bubble Sort, Quick Sort, Merge Sort e Counting Sort) implementados em Java, com versões sequenciais e paralelas. Foram executadas baterias de testes com vetores de 50 000 elementos, cinco amostras por cenário e até 30 threads na versão paralela. Os tempos de execução foram coletados automaticamente em arquivos CSV e analisados para identificar ganhos e perdas de paralelização. Observou-se que Quick Sort e Merge Sort se beneficiaram fortemente do paralelismo, enquanto Bubble Sort e Counting Sort apresentaram degradação, evidenciando a influência da complexidade algorítmica e do custo de coordenação entre threads.

## Introdução
O projeto foi concebido para comparar empiricamente algoritmos clássicos de ordenação em contextos seriais e paralelos, explorando boas práticas de programação concorrente em Java. As versões sequenciais seguem implementações tradicionais descritas em Cormen et al. \[1], enquanto as paralelas utilizam padrões de divisão e conquista com `ForkJoinPool` (Quick Sort e Merge Sort) e abordagens de partição por blocos para Bubble Sort e Counting Sort. A aplicação oferece uma interface Swing que permite selecionar dinamicamente o algoritmo, o perfil de dados e o número de threads, exibindo gráficos animados com médias de tempo por execução.

## Metodologia
A metodologia adotada contemplou:
- Geração de vetores com distribuição aleatória por meio de um gerador determinístico.
- Execução de cinco amostras para cada combinação algoritmo × modo (sequencial/paralelo) × número de threads, garantindo reprodutibilidade.
- Medição do tempo de execução em microssegundos com `System.nanoTime`, convertendo para milissegundos.
- Registro automático dos resultados no arquivo `resultados/desempenho.csv` por execução, possibilitando inspeção estatística posterior.
- Interface de monitoramento que apresenta a média móvel das execuções e uma tabela com os dados brutos.

Para este relatório foram analisadas as execuções com vetores de 50 000 elementos e 30 threads nas versões paralelas. As médias apresentadas resultam da agregação das cinco amostras por cenário.

## Resultados e Discussão
A Tabela 1 resume os tempos médios observados.

| Algoritmo       | Modo        | Threads | Tempo médio (ms) |
|-----------------|-------------|---------|------------------|
| Bubble Sort     | Sequencial  | 1       | **2572,4**       |
| Bubble Sort     | Paralelo    | 30      | 7915,8           |
| Quick Sort      | Sequencial  | 1       | 5,9              |
| Quick Sort      | Paralelo    | 30      | **2,6**          |
| Merge Sort      | Sequencial  | 1       | 4,6              |
| Merge Sort      | Paralelo    | 30      | **1,8**          |
| Counting Sort   | Sequencial  | 1       | **0,4**          |
| Counting Sort   | Paralelo    | 30      | 8,2              |

**Percepções principais**

- *Bubble Sort*: o paralelismo introduziu sobrecarga significativa. O algoritmo demanda múltiplas sincronizações e acessos sequenciais, tornando a versão paralela quase três vezes mais lenta que a sequencial.
- *Quick Sort*: a abordagem divide-e-conquista com `ForkJoinPool` reduziu o tempo médio para 2,6 ms, graças à boa divisão dos sub-vetores e ao baixo custo de sincronização na etapa de particionamento \[2].
- *Merge Sort*: a paralelização do procedimento de intercalação apresentou ganhos consistentes. A combinação de limiares de granularidade e uso de memória auxiliar permitiu aproveitar os 30 threads com eficiência \[3].
- *Counting Sort*: apesar de teoricamente paralelizável (contagem por blocos), a fase de redução e escrita no vetor ordenado demandou sincronização intensa; assim, a versão paralela foi cerca de 20 vezes mais lenta que a sequencial.

As figuras capturadas na interface (`docs/figuras/`) ilustram visualmente essas diferenças, com barras comparativas para cada algoritmo.

## Conclusão
Quick Sort e Merge Sort demonstraram ganhos expressivos com paralelização, reforçando que algoritmos com estratégias divide-e-conquista e baixa dependência entre etapas são candidatos naturais a execução paralela. Em contraste, algoritmos com forte dependência sequencial (Bubble Sort) ou com custos de sincronização elevados na fase de agregação (Counting Sort) podem sofrer degradação mesmo com altos números de threads. O framework desenvolvido facilita novas baterias de testes com outros tamanhos de vetor, distribuições de dados e quantidades de threads, contribuindo para uma compreensão prática da programação concorrente em Java.

## Referências
\[1] T. H. Cormen et al., *Introduction to Algorithms*, 4. ed., MIT Press, 2022.  
\[2] A. S. Tanenbaum e H. Bos, *Modern Operating Systems*, 4. ed., Pearson, 2015.  
\[3] M. McCool, A. Robison e J. Reinders, *Structured Parallel Programming*, Elsevier, 2012.  
\[4] M. Herlihy e N. Shavit, *The Art of Multiprocessor Programming*, 2. ed., Morgan Kaufmann, 2020.

## Anexos – Códigos das implementações
Repositório com o código-fonte e instruções de execução: <https://github.com/joaoaugustocz/ProjetoSortAV2> (substitua pelo link definitivo do seu repositório).
