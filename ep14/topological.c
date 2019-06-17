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
 * ADT Topological é uma "representação topológica" de digrafo.
 * Esta implementação usa ADT Digraph do EP13.
 *  
 * Busque inspiração em: 
 *
 *   https://algs4.cs.princeton.edu/42digraph/
 *   https://algs4.cs.princeton.edu/42digraph/DepthFirstOrder.java
 *   https://algs4.cs.princeton.edu/42digraph/Topological.java
 *   https://algs4.cs.princeton.edu/42digraph/DirectedCycle.java
 * 
 * TOPOLOGICAL
 *
 * Topological é uma ¨representação topológica" de um dado digrafo.
 * 
 * As principais operações são: 
 *
 *      - hasCycle(): indica se o digrafo tem um ciclo (DirectedCycle.java)
 *      - isDag(): indica se o digrafo é acyclico (Topological.java)
 *
 *      - pre(): retorna a numeração pré-ordem de um vértice em relação a uma dfs 
 *               (DepthFirstOrder.java)
 *      - pos(): retorna a numareção pós-ordem de um vértice em relação a uma dfs
 *               (DepthFirstOrder.java)
 *      - rank(): retorna a numeração topológica de um vértice (Topological.java)
 * 
 *      - preorder(): itera sobre todos os vértices do digrafo em pré-ordem
 *                    (em relação a uma dfs, DepthFirstOrder.java)
 *      - postorder(): itera sobre todos os vértices do digrafo em pós-ordem
 *                    (em relação a uma dfs, ordenação topologica reversa, 
 *                     DepthFirstOrder.java)
 *      - order(): itera sobre todos os vértices do digrafo em ordem  
 *                 topologica (Topological.java)
 *      - cycle(): itera sobre os vértices de um ciclo (DirectedCycle.java)
 *
 * O construtor e "destrutor" da classe consomem tempo linear..
 *
 * Cada chama das demais operações consome tempo constante.
 *
 * O espaço gasto por esta ADT é proporcional ao número de vértices V do digrafo.
 * 
 * Para documentação adicional, ver 
 * https://algs4.cs.princeton.edu/42digraph, Seção 4.2 de
 * Algorithms, 4th Edition por Robert Sedgewick e Kevin Wayne.
 *
 */

/* interface para o uso da funcao deste módulo */
#include "topological.h"

#include "digraph.h" /* Digraph, vDigraph(), eDigraph(), adj(), ... */
#include "bag.h"     /* add() e itens() */
#include "util.h"    /* emalloc(), ecalloc(), ERRO(), AVISO() */

#include <stdlib.h>  /* free() */

#undef DEBUG
#ifdef DEBUG
#include <stdio.h>   /* printf(): para debugging */
#endif

/*----------------------------------------------------------*/
/* 
 * Estrutura básica de um Topological
 * 
 */
struct topological {
    Bool* onStack;
    Bool* cycMarked;
    Bool* marked;
    int* edgeTo;
    Bag cycle;

    int preCounter;
    int postCounter;
    
    int* pre;
    int* post;
    Bool* ordMarked;

    Bag invertedPreorder;
    Bag invertedPostorder;

    Bag preorder;
    Bag postorder;

    int* rank;
    Bag order;
};

struct digraph {
   int V; //numero de vertices
   int E; //numero de arestas
   Bag* adj; // ponteiro para o vetor de listas, representadas por bags
   int* indegree; // lista de lista de inteiros, indegree[v] = leque de entrada do vértice v
};

struct bag {
    int n;
    struct node* first; 
    struct node* lastNodeReturned;
};

struct node {
    vertex item;
    struct node* next;
    struct node* prev;
};

/*------------------------------------------------------------*/
/* 
 * Protótipos de funções administrativas: tem modificador 'static'
 * 
 */

static void dfsCheckOrder(Topological ts, Digraph G, int v);

static void dfsCheckCycle(Topological ts, Digraph G, int v);

static Bag createInvertedBag(Bag bag);
/*-----------------------------------------------------------*/
/*
 *  newTopologica(G)
 *
 *  RECEBE um digrafo G.
 *  RETORNA uma representação topológica de G.
 * 
 */
Topological
newTopological(Digraph G) {
    Topological newTopo = ecalloc(1, sizeof(struct topological));

    int V = G->V;
    newTopo->cycMarked = ecalloc(V, sizeof(Bool));
    newTopo->onStack = ecalloc(V, sizeof(Bool));
    newTopo->edgeTo = ecalloc(V, sizeof(int));
    newTopo->cycle = newBag();

    for (int v = 0; v < V; v++) {
        if (!newTopo->cycMarked[v] &&
        newTopo->cycle->n == 0) {
            dfsCheckCycle(newTopo, G, v);
        }
    }

    newTopo->preCounter = 0;
    newTopo->postCounter = 0;

    newTopo->pre = ecalloc(V, sizeof(int));
    newTopo->post = ecalloc(V, sizeof(int));
    newTopo->ordMarked = ecalloc(V, sizeof(Bool));

    newTopo->invertedPreorder = newBag();
    newTopo->invertedPostorder = newBag();

    for (int v = 0; v < V; v++) {
        if (!newTopo->ordMarked[v]) {
            dfsCheckOrder(newTopo, G, v);
        }
    }

    newTopo->preorder = createInvertedBag(newTopo->invertedPreorder);
    newTopo->postorder = createInvertedBag(newTopo->invertedPostorder);

    newTopo->rank = ecalloc(V, sizeof(int));

    if (!hasCycle(newTopo)) {
        newTopo->order = newTopo->invertedPostorder;
        int i = 0;
        for (int v = itens(newTopo->order, TRUE); v >= 0; v = itens(newTopo->order, FALSE)) {
            newTopo->rank[v] = i++;
        }
    }
    
    return newTopo;
}

/*-----------------------------------------------------------*/
/*
 *  freeTopological(TS)
 *
 *  RECEBE uma representação topologica TS.
 *  DEVOLVE ao sistema toda a memória usada por TS.
 *
 */
void  
freeTopological(Topological ts) {
    free(ts->rank);
    freeBag(ts->postorder);
    freeBag(ts->preorder);
    freeBag(ts->invertedPostorder);
    freeBag(ts->invertedPreorder);
    free(ts->ordMarked);
    free(ts->post);
    free(ts->pre);
    freeBag(ts->cycle);
    free(ts->edgeTo);
    free(ts->onStack);
    free(ts->cycMarked);
    free(ts);
}    

/*------------------------------------------------------------*/
/*
 *  OPERAÇÕES: 
 *
 */

/*-----------------------------------------------------------*/
/* 
 *  HASCYCLE(TS)
 *
 *  RECEBE uma representação topológica TS de um digrafo;
 *  RETORNA TRUE seu o digrafo possui um ciclo e FALSE em caso 
 *  contrário.
 *
 */
Bool
hasCycle(Topological ts) {
    return ts->cycle->n != 0;
}

/*-----------------------------------------------------------*/
/* 
 *  ISDAG(TS)
 *
 *  RECEBE um representação topológica TS de um digrafo.
 *  RETORNA TRUE se o digrafo for um DAG e FALSE em caso 
 *  contrário.
 *
 */
Bool
isDag(Topological ts) {
    return ts->cycle->n == 0;
}

/*-----------------------------------------------------------*/
/* 
 *  PRE(TS, V)
 *
 *  RECEBE uma representação topológica TS de um digrafo e um 
 *  vértice V.
 *  RETORNA a numeração pré-ordem de V em TS.
 *
 */
int
pre(Topological ts, vertex v) {
    return ts->pre[v];
}

/*-----------------------------------------------------------*/
/* 
 *  POST(TS, V)
 *
 *  RECEBE uma representação topológica TS de um digrafo e um 
 *  vértice V.
 *  RETORNA a numeração pós-ordem de V em TS.
 *
 */
int
post(Topological ts, vertex v) {
    return ts->post[v];
}

/*-----------------------------------------------------------*/
/* 
 *  RANK(TS, V)
 *
 *  RECEBE uma representação topológica TS de um digrafo e um 
 *  vértice V.
 *  RETORNA a posição de V na ordenação topológica em TS;
 *  retorna -1 se o digrafo não for um DAG.
 *
 */
int
rank(Topological ts, vertex v) {
    if(!isDag(ts)) {
        return -1;
    }
    return ts->rank[v];
}

/*-----------------------------------------------------------*/
/* 
 *  PREORDER(TS, INIT)
 * 
 *  RECEBE uma representação topológica TS de um digrafo e um 
 *  Bool INIT.
 *
 *  Se INIT é TRUE,  PREORDER() RETORNA o primeiro vértice na ordenação pré-ordem de TS.
 *  Se INIT é FALSE, PREORDER() RETORNA o vértice sucessor do último vértice retornado
 *                   na ordenação pré-ordem de TS; se todos os vértices já foram retornados, 
 *                   a função retorna -1.
 */
vertex
preorder(Topological ts, Bool init) {
    return itens(ts->preorder, init);
}

/*-----------------------------------------------------------*/
/* 
 *  POSTORDER(TS, INIT)
 * 
 *  RECEBE uma representação topológica TS de um digrafo e um 
 *  Bool INIT.
 *
 *  Se INIT é TRUE,  POSTORDER() RETORNA o primeiro vértice na ordenação pós-ordem de TS.
 *  Se INIT é FALSE, POSTORDER() RETORNA o vértice sucessor do último vértice retornado
 *                   na ordenação pós-ordem de TS; se todos os vértices já foram retornados, 
 *                   a função retorna -1.
 */
vertex
postorder(Topological ts, Bool init) {
    return itens(ts->postorder, init);
}

/*-----------------------------------------------------------*/
/* 
 *  ORDER(TS, INIT)
 * 
 *  RECEBE uma representação topológica TS de um digrafo e um Bool INIT.
 *
 *  Se INIT é TRUE,  ORDER() RETORNA o primeiro vértice na ordenação topológica 
 *                   de TS.
 *  Se INIT é FALSE, ORDER() RETORNA o vértice sucessor do último vértice retornado
 *                   na ordenação topológica de TS; se todos os vértices já foram 
 *                   retornados, a função retorna -1.
 *
 *  Se o digrafo _não_ é um DAG, ORDER() RETORNA -1.
 */
vertex
order(Topological ts, Bool init) {
    if(!isDag(ts)) {
        return -1;
    }
    return itens(ts->order, init);
}

/*-----------------------------------------------------------*/
/* 
 *  CYCLE(TS, INIT)
 * 
 *  RECEBE uma representação topológica TS de um digrafo e um Bool INIT.
 *
 *  Se INIT é TRUE,  CYCLE() RETORNA um vértice em um ciclo do digrafo.
 *  Se INIT é FALSE, CYCLE() RETORNA o vértice  no ciclo que é sucessor do 
 *                   último vértice retornado; se todos os vértices no ciclo já 
 *                   foram retornados, a função retorna -1.
 *
 *  Se o digrafo é um DAG, CYCLE() RETORNA -1.
 *
 */
vertex
cycle(Topological ts, Bool init) {
    if(isDag(ts)) {
        return -1;
    }
    return itens(ts->cycle, init);
}


/*------------------------------------------------------------*/
/* 
 * Implementaçao de funções administrativas: têm o modificador 
 * static.
 */

static void dfsCheckOrder(Topological ts, Digraph G, int v) {
    ts->ordMarked[v] = TRUE;
    ts->pre[v] = ts->preCounter++;
    add(ts->invertedPreorder, v);

    Bag vAdj = G->adj[v];
    for (int w = itens(vAdj, TRUE); w >= 0; w = itens(vAdj, FALSE)) {
        if (!ts->ordMarked[w]) {
            dfsCheckOrder(ts, G, w);
        }
    }
    add(ts->invertedPostorder, v);
    ts->post[v] = ts->postCounter++;
}

static void dfsCheckCycle(Topological ts, Digraph G, int v) {
    ts->onStack[v] = TRUE;
    ts->cycMarked[v] = TRUE;

    Bag vAdj = G->adj[v];
    for (int w = itens(vAdj, TRUE); w >= 0; w = itens(vAdj, FALSE)) {
        // short circuit if directed cycle found
        if (ts->cycle->n != 0) return;

        // found new vertex, so recur
        else if (!ts->cycMarked[w]) {
            ts->edgeTo[w] = v;
            dfsCheckCycle(ts, G, w);
        }

        // trace back directed cycle
        else if (ts->onStack[w]) {
            for (int x = v; x != w; x = ts->edgeTo[x]) {
                add(ts->cycle, x);
            }
            add(ts->cycle, w);
        }
    }
    ts->onStack[v] = FALSE;
}

static Bag createInvertedBag(Bag bag) {
    Bag newB = newBag();

    for (int v = itens(bag, TRUE); v >= 0; v = itens(bag, FALSE)) {
        add(newB, v);
    }

    return newB;
}   