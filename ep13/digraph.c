/****************************************************************
    Nome: Eduardo Yukio Rodrigues
    NUSP: 8988702

    Ao preencher esse cabeçalho com o meu nome e o meu número USP,
    declaro que todas as partes originais desse exercício programa (EP)
    foram desenvolvidas e implementadas por mim e que portanto não 
    constituem desonestidade acadêmica ou plágio.
    Declaro também que sou responsável por todas as cópias desse
    programa e que não distribui ou facilitei a sua distribuição.
    Estou ciente que os casos de plágio e desonestidade acadêmica
    serão tratados segundo os critérios divulgados na página da 
    disciplina.
    Entendo que EPs sem assinatura devem receber nota zero e, ainda
    assim, poderão ser punidos por desonestidade acadêmica.

    Abaixo descreva qualquer ajuda que você recebeu para fazer este
    EP.  Inclua qualquer ajuda recebida por pessoas (inclusive
    monitoras e colegas). Com exceção de material de MAC0323, caso
    você tenha utilizado alguma informação, trecho de código,...
    indique esse fato abaixo para que o seu programa não seja
    considerado plágio ou irregular.

    Exemplo:

        A monitora me explicou que eu devia utilizar a função xyz().

        O meu método xyz() foi baseada na descrição encontrada na 
        página https://www.ime.usp.br/~pf/algoritmos/aulas/enumeracao.html.

    Descrição de ajuda ou indicação de fonte:



    Se for o caso, descreva a seguir 'bugs' e limitações do seu programa:
        Este EP foi feito em C99

****************************************************************/


/*
 * MAC0323 Algoritmos e Estruturas de Dados II
 * 
 * ADT Digraph implementada atrevés de vetor de listas de adjacência.
 * As listas de adjacência são bag de ints que são mais restritos 
 * que as bags genéricas do EP12. Veja a api bag.h e simplifique 
 * o EP12 de acordo. 
 *  
 * Busque inspiração em: 
 *
 *    https://algs4.cs.princeton.edu/42digraph/ (Graph representation)
 *    https://algs4.cs.princeton.edu/42digraph/Digraph.java.html
 * 
 * DIGRAPH
 *
 * Digraph representa um grafo orientado de vértices inteiros de 0 a V-1. 
 * 
 * As principais operações são: add() que insere um arco no digrafo, e
 * adj() que itera sobre todos os vértices adjacentes a um dado vértice.
 * 
 * Arcos paralelos e laços são permitidos.
 * 
 * Esta implementação usa uma representação de _vetor de listas de adjacência_,
 * que  é uma vetor de objetos Bag indexado por vértices. 

 * ATENÇÃO: Por simplicidade esses Bag podem ser int's e não de Integer's.
 *
 * Todas as operações consomen no pior caso tempo constante, exceto
 * iterar sobre os vértices adjacentes a um determinado vértice, cujo 
 * consumo de tempo é proporcional ao número de tais vértices.
 * 
 * Para documentação adicional, ver 
 * https://algs4.cs.princeton.edu/42digraph, Seção 4.2 de
 * Algorithms, 4th Edition por Robert Sedgewick e Kevin Wayne.
 *
 */

/* interface para o uso da funcao deste módulo */
#include "digraph.h"


#include "bag.h"     /* add() e itens() */
#include <stdio.h>   /* fopen(), fclose(), fscanf(), ... */
#include <stdlib.h>  /* free() */
#include <string.h>  /* memcpy() */
#include "util.h"    /* emalloc(), ecalloc() */

#undef DEBUG
#ifdef DEBUG
#include <stdio.h>   /* printf(): para debuging */
#endif

/*----------------------------------------------------------*/
/* 
 * Estrutura básica de um Digraph
 * 
 * Implementação com vetor de listas de adjacência.
 */
struct digraph {
   int V; //numero de vertices
   int E; //numero de arestas
   Bag* adj; // ponteiro para o vetor de listas, representadas por bags
   int** indegree; // lista de lista de inteiros, indegree[v] = leque de entrada do vértice v
};

/*------------------------------------------------------------*/
/* 
 * Protótipos de funções administrativas: tem modificador 'static'
 * 
 */

/*-----------------------------------------------------------*/
/*
 *  newDigraph(V)
 *
 *  RECEBE um inteiro V.
 *  RETORNA um digrafo com V vértices e 0 arcos.
 * 
 */
Digraph
newDigraph(int V) {
    Digraph emptyGraph = ecalloc(1, sizeof(Digraph));
    emptyGraph->V = V;
    emptyGraph->E = 0;

    emptyGraph->adj = ecalloc(V, sizeof(Bag));
    emptyGraph->indegree = ecalloc(V, sizeof(int*));
    for(int i = 0; i < V; i++){
        emptyGraph->adj[i] = newBag();
        emptyGraph->indegree[i] = ecalloc(1, sizeof(int));
    }

    return emptyGraph;
}

/*-----------------------------------------------------------*/
/*
 *  cloneDigraph(G)
 *
 *  RECEBE um digrafo G.
 *  RETORNA um clone de G.
 * 
 */
Digraph
cloneDigraph(Digraph G) {
    int V = G->V;
    Digraph cloneGraph = newDigraph(V);

    // Bag bagClone = emalloc(sizeof(Bag));
    // memcpy(adjClone, G->adj, V*sizeof(Bag));
    // cloneGraph->adj = adjClone;

    // int** indegreeClone = emalloc(V*sizeof(int*));
    // memcpy(indegreeClone, G->indegree, V*sizeof(int*));
    // cloneGraph->indegreeClone = indegree;

    for(int v = 0; v < V; v++){
        for(int w = 0; w < G->adj[v]->size; w++){
            addEdge(cloneGraph, v, w);
        }
    }

    return cloneGraph;
}

/*-----------------------------------------------------------*/
/*
 *  reverseDigraph(G)
 *
 *  RECEBE um digrafo G.
 *  RETORNA o digrafo R que é o reverso de G: 
 *
 *      v-w é arco de G <=> w-v é arco de R.
 * 
 */
Digraph
reverseDigraph(Digraph G) {
    int V = G->V;
    Digraph reverseGraph = newDigraph(V);

    for(int v = 0; v < V; v++){
        for(int w = 0; w < size(G->adj[v]); w++){
            addEdge(reverseGraph, w, v);
        }
    }

    return reverseGraph;
}

/*-----------------------------------------------------------*/
/*
 *  readDigraph(NOMEARQ)
 *
 *  RECEBE uma stringa NOMEARQ.
 *  RETORNA o digrafo cuja representação está no arquivo de nome NOMEARQ.
 *  O arquivo contém o número de vértices V, seguido pelo número de arestas E,
 *  seguidos de E pares de vértices, com cada entrada separada por espaços.
 *
 *  Veja os arquivos  tinyDG.txt, mediumDG.txt e largeDG.txt na página do 
 *  EP e que foram copiados do algs4, 
 * 
 */
Digraph
readDigraph(String nomeArq) {
    FILE *fp;
    fp = fopen(nomeArq, "r");
    if (fp == NULL) {
        printf ("Error creating file\n");
        return NULL;
    }

    String line = getLine(fp);
    if(line == NULL){
        printf ("File is empty\n");
        return NULL;
    }

    Digraph newGraph = newDigraph(atoi(line));
    line = getLine(fp);
    int E = atoi(line);
    for(int i = 0; i < E; i++){
        line = getLine(fp);
        char* vStr = strtok(line, " ");
        // nao sei se pode dar ruim se tiver mais de 
        // um espaço, *** TEM QUE VER ***
        char* wStr = strtok(NULL, " ");
        addEdge(newGraph, atoi(vStr), atoi(wStr));
    }

    fclose(fp);
    return newGraph;
}


/*-----------------------------------------------------------*/
/*
 *  freeDigraph(G)
 *
 *  RECEBE um digrafo G e retorna ao sistema toda a memória 
 *  usada por G.
 *
 */
void  
freeDigraph(Digraph G)
{
}    

/*------------------------------------------------------------*/
/*
 * OPERAÇÕES USUAIS: 
 *
 *     - vDigraph(), eDigraph(): número de vértices e arcos
 *     - addEdge(): insere um arco
 *     - adj(): itera sobre os vizinhos de um dado vértice
 *     - outDegree(), inDegree(): grau de saída e de entrada
 *     - toString(): usada para exibir o digrafo 
 */

/*-----------------------------------------------------------*/
/* 
 *  VDIGRAPH(G)
 *
 *  RECEBE um digrafo G e RETORNA seu número de vertices.
 *
 */
int
vDigraph(Digraph G) {
    return G->V;
}

/*-----------------------------------------------------------*/
/* 
 *  EDIGRAPH(G)
 *
 *  RECEBE um digrafo G e RETORNA seu número de arcos (edges).
 *
 */
int
eDigraph(Digraph G) {
    return G->E;
}

/*-----------------------------------------------------------*/
/*
 *  addEdge(G, V, W)
 * 
 *  RECEBE um digrafo G e vértice V e W e INSERE o arco V-W  
 *  em G.
 *
 */
void  
addEdge(Digraph G, vertex v, vertex w) {
    add(G->adj[v], w);
    G->indegree[w]++;
    G->E++;
}    


/*-----------------------------------------------------------*/
/* 
 *  ADJ(G, V, INIT)
 * 
 *  RECEBE um digrafo G, um vértice v de G e um Bool INIT.
 *
 *  Se INIT é TRUE,  ADJ() RETORNA o primeiro vértice na lista de adjacência de V.
 *  Se INIT é FALSE, ADJ() RETORNA o sucessor na lista de adjacência de V do 
 *                   último vértice retornado.
 *  Se a lista de adjacência de V é vazia ou não há sucessor do último vértice 
 *  retornada, ADJ() RETORNA -1.
 *
 *  Se entre duas chamadas de ADJ() a lista de adjacência de V é alterada, 
 *  o comportamento é  indefinido. 
 *  
 */
int 
adj(Digraph G, vertex v, Bool init) {
    return itens(G->adj[v], init);
}

/*-----------------------------------------------------------*/
/*
 *  outDegree(G, V)
 * 
 *  RECEBE um digrafo G e vértice V.
 *  RETORNA o número de arcos saindo de V.
 *
 */
int
outDegree(Digraph G, vertex v) {
    return size(G->adj[v]);
}

/*-----------------------------------------------------------*/
/*
 *  inDegree(G, V)
 * 
 *  RECEBE um digrafo G e vértice V.
 *  RETORNA o número de arcos entrando em V.
 *
 */
int
inDegree(Digraph G, vertex v) {
    return *G->indegree[v];
}


/*-----------------------------------------------------------*/
/*
 *  toString(G)
 * 
 *  RECEBE um digrafo G.
 *  RETORNA uma string que representa G. Essa string será usada
 *  para exibir o digrafo: printf("%s", toString(G)); 
 *    
 *  Sigestão: para fazer esta função inspire-se no método 
 *  toString() da classe Digraph do algs4.
 */
String
toString(Digraph G) {
    int LEN = G->V * G->V * 100 + 1000;
    char* stringGraph = emalloc(LEN*sizeof(char)); 

    char buffer[50];
    snprintf(buffer, 50, "%d vertices, %d edges\n", G->V, G->E);
    strcat(stringGraph, buffer);

    for(int v = 0; v < G->V; v++){
        snprintf(buffer, 50, "%d: ", v);
        strcat(stringGraph, buffer);

        Bag currBag = G->adj[v];
        vertex currItem = itens(currBag, TRUE);
        while(currItem >= 0) {
            snprintf(buffer, 50, "%d ", currItem);
            strcat(stringGraph, buffer);
            currItem = itens(currBag, FALSE);
        }
        strcat(stringGraph, "\n");
    }

    return stringGraph;
}

/*------------------------------------------------------------*/
/* 
 * Implementaçao de funções administrativas: têm o modificador 
 * static.
 */

