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

****************************************************************/
/*
 * MAC0323 Estruturas de Dados e Algoritmo II
 * 
 * Tabela de simbolos implementada atraves de uma BST rubro-negra
 *
 *     https://algs4.cs.princeton.edu/33balanced/RedBlackBST.java.html
 * 
 * As chaves e valores desta implementação são mais ou menos
 * genéricos
 */

/* interface para o uso da funcao deste módulo */
#include "redblackst.h"  

#include <stdlib.h>  /* free() */
#include <string.h>  /* memcpy() */
#include "util.h"    /* emalloc(), ecalloc() */

#undef DEBUG
#ifdef DEBUG
#include <stdio.h>   /* printf(): para debug */
#endif

/*
 * CONSTANTES 
 */
#define RED   TRUE
#define BLACK FALSE 
#define mathMax(x,y) (((x)>(y)) ? (x) : (y))
/*const int INIT_SIZE = 65536;*/

/*----------------------------------------------------------*/
/* 
 * Estrutura Básica da Tabela de Símbolos: 
 * 
 * implementação com árvore rubro-negra
 */
typedef struct node Node;
struct redBlackST {
    struct node *root;
    int (*compar)(const void*,const void*);
    size_t n;
    void **keysArray;
    int keysIndex;
};

/*----------------------------------------------------------*/
/* 
 * Estrutura de um nó da árvore
 *
 */
struct node {
    void* key;
    void* val;
    Bool color;
    struct node *left, *right;
    int size;
    size_t nKey;
    size_t nVal;
};

/*------------------------------------------------------------*/
/* 
*  Protótipos de funções administrativas.
* 
*  Entre essa funções estão isRed(), rotateLeft(), rotateRight(),
*  flipColors(), moveRedLeft(), moveRedRight() e balance().
* 
*  Não deixe de implmentar as funções chamadas pela função 
*  check(): isBST(), isSizeConsistent(), isRankConsistent(),
*  is23(), isBalanced().
*
*/

int
rankRecursive(RedBlackST st, const void *key, struct node *x);

struct node *
rotateLeft(struct node *h);

struct node *
rotateRight(struct node *h);

Bool
isRed(struct node *x);

void
flipColors(struct node *h);

struct node *
putRecursive(struct node *h, const void* key, const void* val, RedBlackST st, size_t nKey, size_t nVal);

void *
minRecursive(struct node *x);

void
prepareKeys(RedBlackST st, struct node *x, void *lo, void *hi);

void *
maxRecursive(struct node *x);

struct node *
deleteRecursive(RedBlackST st, struct node *h, const void *key);

struct node *
deleteMinRecursive(RedBlackST st, struct node *h);

struct node *
deleteMaxRecursive(RedBlackST st, struct node *h);

struct node *
moveRedLeft(struct node *h);

struct node *
moveRedRight(struct node *h);

struct node *
balance(struct node *h);

Bool
isBSTRecursive(RedBlackST st, struct node *x, void* min, void* max);

Bool
isSizeConsistentRecursive(struct node *x);

Bool
is23Recursive(RedBlackST st, struct node *x);

Bool
isBalancedRecursive(struct node *x, int black);

struct node *
selectRecursive(struct node *x, int k);

int
sizeProtected(struct node *x);

int
heightRecursive(struct node *x);
/*---------------------------------------------------------------*/
static Bool
isBST(RedBlackST st);

/*---------------------------------------------------------------*/
static Bool
isSizeConsistent(RedBlackST st);

/*---------------------------------------------------------------*/
static Bool
isRankConsistent(RedBlackST st);

/*---------------------------------------------------------------*/
static Bool
is23(RedBlackST st);

/*---------------------------------------------------------------*/
static Bool
isBalanced(RedBlackST st);

/*-----------------------------------------------------------*/
/*
 *  initST(COMPAR)
 *
 *  RECEBE uma função COMPAR() para comparar chaves.
 *  RETORNA (referência/ponteiro para) uma tabela de símbolos vazia.
 *
 *  É esperado que COMPAR() tenha o seguinte comportamento:
 *
 *      COMPAR(key1, key2) retorna um inteiro < 0 se key1 <  key2
 *      COMPAR(key1, key2) retorna 0              se key1 == key2
 *      COMPAR(key1, key2) retorna um inteiro > 0 se key1 >  key2
 * 
 *  TODAS OS OPERAÇÕES da ST criada utilizam a COMPAR() para comparar
 *  chaves.
 * 
 */
RedBlackST
initST(int (*compar)(const void *key1, const void *key2))
{
    RedBlackST st = ecalloc(1, sizeof(struct redBlackST));
    st->keysArray = ecalloc(1, sizeof(void*));
    st->compar = compar;
    st->keysIndex = 1;
    st->n = 0;


    /*
    st->root = ecalloc(1, sizeof(struct node));
    st->root->key = ecalloc(1, sizeof(void*));
    st->root->val = ecalloc(1, sizeof(void*));
    st->root->left = NULL;
    st->root->right = NULL;
    st->root->size = 0;
    */

/*    st->maxSTLength = INIT_SIZE*sizeof(void*);*/

    return st;
}

/*-----------------------------------------------------------*/
/*
 *  freeST(ST)
 *
 *  RECEBE uma RedBlackST  ST e devolve ao sistema toda a memoria 
 *  utilizada por ST.
 *
 */
void  
freeST(RedBlackST st)
{
    int i;
    for(i = 0; i <= st->keysIndex; i++){
        free(st->keysArray[i]);
    }
    free(st->keysArray);

    free(st);
}


/*------------------------------------------------------------*/
/*
 * OPERAÇÕES USUAIS: put(), get(), contains(), delete(),
 * size() e isEmpty().
 */

/*-----------------------------------------------------------*/
/*
 *  put(ST, KEY, NKEY, VAL, NVAL)
 * 
 *  RECEBE a tabela de símbolos ST e um par KEY-VAL e procura a KEY na ST.
 *
 *     - se VAL é NULL, a entrada da chave KEY é removida da ST  
 *  
 *     - se KEY nao e' encontrada: o par KEY-VAL é inserido na ST
 *
 *     - se KEY e' encontra: o valor correspondente é atualizado
 *
 *  NKEY é o número de bytes de KEY e NVAL é o número de bytes de NVAL.
 *
 *  Para criar uma copia/clone de KEY é usado o seu número de bytes NKEY.
 *  Para criar uma copia/clode de VAL é usado o seu número de bytes NVAL.
 *
 */

void  
put(RedBlackST st, const void *key, size_t sizeKey, const void *val, size_t sizeVal)
{
    if (val == NULL) {
        delete(st, key);
        return;
    }

    st->root = putRecursive(st->root, key, val, st, sizeKey, sizeVal);
    st->root->color = BLACK;
}    


struct node *
putRecursive(struct node *h, const void* key, const void* val, RedBlackST st, size_t nKey, size_t nVal){
    void *valClone;
    void *keyClone;
    int cmp;

    if (h == NULL) {
        struct node *newNode = ecalloc(1, sizeof(struct node));

        keyClone = emalloc(nKey);
        memcpy(keyClone, key, nKey);
        newNode->key = keyClone;

        valClone = emalloc(nVal);
        memcpy(valClone, val, nVal);
        newNode->val = valClone;

        newNode->left = NULL;
        newNode->right = NULL;
        newNode->size = 1;
        newNode->color = RED;

        newNode->nKey = nKey;
        newNode->nVal = nVal;

        st->n++;
        return newNode;
    }

    cmp = st->compar(key, h->key);
    if (cmp < 0) {
        h->left  = putRecursive(h->left, key, val, st, nKey, nVal); 
    }
    else if (cmp > 0) {
        h->right = putRecursive(h->right, key, val, st, nKey, nVal); 
    }
    else {
        valClone = emalloc(nVal);
        memcpy(valClone, val, nVal);
        free(h->val);
        h->val = valClone;
        h->nVal = nVal;
    }

    /* fix-up any right-leaning links */
    if (isRed(h->right) && !isRed(h->left))
        h = rotateLeft(h);

    if (isRed(h->left)  &&  isRed(h->left->left))
        h = rotateRight(h);

    if (isRed(h->left)  &&  isRed(h->right))     flipColors(h);

    h->size = sizeProtected(h->left) + sizeProtected(h->right) + 1;

    return h;
}

/* make a right-leaning link lean to the left */
struct node *
rotateLeft(struct node *h) {
    struct node *x = h->right;
    h->right = x->left;
    x->left = h;
    x->color = x->left->color;
    x->left->color = RED;
    
    x->size = h->size;
    h->size = sizeProtected(h->left) + sizeProtected(h->right) + 1;
    
    return x;
}

struct node *
rotateRight(struct node *h) {
    /* assert (h != null) && isRed(h.left); */
    struct node *x = h->left;
    h->left = x->right;
    x->right = h;
    x->color = x->right->color;
    x->right->color = RED;
    
    x->size = h->size;
    h->size = sizeProtected(h->left) + sizeProtected(h->right) + 1;
    
    return x;
}

Bool
isRed(struct node *x) {
    if (x == NULL) return FALSE;
    return x->color == RED;
}

/* flip the colors of a node and its two children*/
void
flipColors(struct node *h) {
    /* h must have opposite color of its two children */
    /* assert (h != null) && (h.left != null) && (h.right != null); */
    /* assert (!isRed(h) &&  isRed(h.left) &&  isRed(h.right)) */
    /*    || (isRed(h)  && !isRed(h.left) && !isRed(h.right)); */
    h->color = !h->color;
    h->left->color = !h->left->color;
    h->right->color = !h->right->color;
}

/*-----------------------------------------------------------*/
/*
 *  get(ST, KEY)
 *
 *  RECEBE uma tabela de símbolos ST e uma chave KEY.
 *
 *     - se KEY está em ST, RETORNA NULL;
 *
 *     - se KEY não está em ST, RETORNA uma cópia/clone do valor
 *       associado a KEY.
 * 
 */
void *
get(RedBlackST st, const void *key)
{
    struct node *x = st->root;
    void *valClone;
    int cmp;

    if (isEmpty(st)){
      return NULL;
    }

    while (x != NULL) {
        cmp = st->compar(key, x->key);
        if      (cmp < 0) x = x->left;
        else if (cmp > 0) x = x->right;
        else {
            valClone = emalloc(x->nVal);
            memcpy(valClone, x->val, x->nVal);
            return valClone;
        };
    }
    return NULL;
}


/*-----------------------------------------------------------*/
/* 
 *  CONTAINS(ST, KEY)
 *
 *  RECEBE uma tabela de símbolos ST e uma chave KEY.
 * 
 *  RETORNA TRUE se KEY está na ST e FALSE em caso contrário.
 *
 */
Bool
contains(RedBlackST st, const void *key)
{
    return get(st, key) != NULL;
}

/*-----------------------------------------------------------*/
/* 
 *  DELETE(ST, KEY)
 *
 *  RECEBE uma tabela de símbolos ST e uma chave KEY.
 * 
 *  Se KEY está em ST, remove a entrada correspondente a KEY.
 *  Se KEY não está em ST, faz nada.
 *
 */
void
delete(RedBlackST st, const void *key)
{
    if (!contains(st, key)) return;

    if (!isRed(st->root->left) && !isRed(st->root->right))
        st->root->color = RED;

    st->root = deleteRecursive(st, st->root, key);
    if (!isEmpty(st)) st->root->color = BLACK;
    st->n--;
}

struct node *
deleteRecursive(RedBlackST st, struct node *h, const void *key) {
    struct node *x;

    if (st->compar(key, h->key) < 0)  {
        if (!isRed(h->left) && !isRed(h->left->left))
            h = moveRedLeft(h);
        h->left = deleteRecursive(st, h->left, key);
    }
    else {
        if (isRed(h->left))
            h = rotateRight(h);
        if (st->compar(key, h->key) == 0 && (h->right == NULL))
            return NULL;
        if (!isRed(h->right) && !isRed(h->right->left))
            h = moveRedRight(h);
        if (st->compar(key, h->key) == 0) {
            x = minRecursive(h->right);
            h->key = x->key;
            h->val = x->val;
            /*
            h->val = get(h->right, min(h->right)->key);
            h->key = min(h->right)->key;
            */
            h->right = deleteMinRecursive(st, h->right);
        }
        else h->right = deleteRecursive(st, h->right, key);
    }
    return balance(h);
}


/*-----------------------------------------------------------*/
/* 
 *  SIZE(ST)
 *
 *  RECEBE uma tabela de símbolos ST.
 * 
 *  RETORNA o número de itens (= pares chave-valor) na ST.
 *
 */
int
size(RedBlackST st)
{
    return st->n;
}

int
sizeProtected(struct node *x) {
    if (x == NULL) return 0;
    return x->size;
} 


/*-----------------------------------------------------------*/
/* 
 *  ISEMPTY(ST, KEY)
 *
 *  RECEBE uma tabela de símbolos ST.
 * 
 *  RETORNA TRUE se ST está vazia e FALSE em caso contrário.
 *
 */
Bool
isEmpty(RedBlackST st)
{
    return st->n == 0;
}

/*------------------------------------------------------------*/
/*
 * OPERAÇÕES PARA TABELAS DE SÍMBOLOS ORDENADAS: 
 * min(), max(), rank(), select(), deleteMin() e deleteMax().
 */

/*-----------------------------------------------------------*/
/*
 *  MIN(ST)
 * 
 *  RECEBE uma tabela de símbolos ST e RETORNA uma cópia/clone
 *  da menor chave na tabela.
 *
 *  Se ST está vazia RETORNA NULL.
 *
 */
void *
min(RedBlackST st)
{
    struct node *minNode;
    void *keyClone;
    if(isEmpty(st)){
        return NULL;
    }

    minNode = minRecursive(st->root);

    keyClone = emalloc(minNode->nKey);
    memcpy(keyClone, minNode->key, minNode->nKey);

    return keyClone;
}

void *
minRecursive(struct node *x) {
    if (x->left == NULL) return x; 
    else return minRecursive(x->left);
}


/*-----------------------------------------------------------*/
/*
 *  MAX(ST)
 * 
 *  RECEBE uma tabela de símbolos ST e RETORNA uma cópia/clone
 *  da maior chave na tabela.
 *
 *  Se ST está vazia RETORNA NULL.
 *
 */
void *
max(RedBlackST st)
{
    struct node *maxNode;
    void *keyClone;
    if(isEmpty(st)){
        return NULL;
    }

    maxNode = maxRecursive(st->root);

    keyClone = emalloc(maxNode->nKey);
    memcpy(keyClone, maxNode->key, maxNode->nKey);

    return keyClone;
}

void *
maxRecursive(struct node *x) {
    if (x->right == NULL) return x; 
    else return maxRecursive(x->right);
}


/*-----------------------------------------------------------*/
/*
 *  RANK(ST, KEY)
 * 
 *  RECEBE uma tabela de símbolos ST e uma chave KEY.
 *  RETORNA o número de chaves em ST menores que KEY.
 *
 *  Se ST está vazia RETORNA NULL.
 *
 */
int
rank(RedBlackST st, const void *key)
{
    if(isEmpty(st)) return 0;
    if(st == NULL) return EXIT_FAILURE;

    return rankRecursive(st, key, st->root);
} 

int
rankRecursive(RedBlackST st, const void *key, struct node *x){
    int cmp;
    
    if (x == NULL) return 0; 
    
    cmp = st->compar(key, x->key); 
    if      (cmp < 0) return rankRecursive(st, key, x->left); 
    else if (cmp > 0) return 1 + sizeProtected(x->left) + rankRecursive(st, key, x->right); 
    else              return sizeProtected(x->left); 
}

/*-----------------------------------------------------------*/
/*
 *  SELECT(ST, K)
 * 
 *  RECEBE uma tabela de símbolos ST e um inteiro K >= 0.
 *  RETORNA a (K+1)-ésima menor chave da tabela ST.
 *
 *  Se ST não tem K+1 elementos RETORNA NULL.
 *
 */
void *
select(RedBlackST st, int k)
{
    struct node *x;

    if (k < 0 || k >= sizeProtected(st->root)) {
        return NULL;
    }
    x = selectRecursive(st->root, k);
    return x->key;
}

struct node *
selectRecursive(struct node *x, int k) {
    int t = sizeProtected(x->left); 
    if      (t > k) return selectRecursive(x->left,  k); 
    else if (t < k) return selectRecursive(x->right, k-t-1); 
    else            return x;
} 


/*-----------------------------------------------------------*/
/*
 *  deleteMIN(ST)
 * 
 *  RECEBE uma tabela de símbolos ST e remove a entrada correspondente
 *  à menor chave.
 *
 *  Se ST está vazia, faz nada.
 *
 */
void
deleteMin(RedBlackST st)
{
    if(isEmpty(st)) return;

    if (!isRed(st->root->left) && !isRed(st->root->right))
        st->root->color = RED;

    st->root = deleteMinRecursive(st, st->root);
    if (!isEmpty(st)) st->root->color = BLACK;
    st->n--;
}

struct node *
deleteMinRecursive(RedBlackST st, struct node *h){
    if (h->left == NULL)
        return NULL;

    if (!isRed(h->left) && !isRed(h->left->left))
        h = moveRedLeft(h);

    h->left = deleteMinRecursive(st, h->left);
    return balance(h);
}


/*-----------------------------------------------------------*/
/*
 *  deleteMAX(ST)
 * 
 *  RECEBE uma tabela de símbolos ST e remove a entrada correspondente
 *  à maior chave.
 *
 *  Se ST está vazia, faz nada.
 *
 */
void
deleteMax(RedBlackST st)
{
    if(isEmpty(st)) return;

    if (!isRed(st->root->left) && !isRed(st->root->right))
        st->root->color = RED;

    st->root = deleteMaxRecursive(st, st->root);
    if (!isEmpty(st)) st->root->color = BLACK;
    st->n--;
}

struct node *
deleteMaxRecursive(RedBlackST st, struct node *h){
    if (isRed(h->left))
        h = rotateRight(h);

    if (h->right == NULL)
        return NULL;

    if (!isRed(h->right) && !isRed(h->right->left))
        h = moveRedRight(h);

    h->right = deleteMaxRecursive(st, h->right);

    return balance(h);
}
/*-----------------------------------------------------------*/
/* 
 *  KEYS(ST, INIT)
 * 
 *  RECEBE uma tabela de símbolos ST e um Bool INIT.
 *
 *  Se INIT é TRUE, KEYS() RETORNA uma cópia/clone da menor chave na ST.
 *  Se INIT é FALSE, KEYS() RETORNA a chave sucessora da última chave retornada.
 *  Se ST está vazia ou não há sucessora da última chave retornada, KEYS() retorna NULL.
 *
 *  Se entre duas chamadas de KEYS() a ST é alterada, o comportamento é 
 *  indefinido. 
 *  
 */
void * 
keys(RedBlackST st, Bool init)
{
    struct node *minNode;
    struct node *maxNode;

    if(init){
        /*
        int i;
        for(i = 0; i < st->keysIndex; i++){
            free(st->keysArray[i]);
        }
        */
        free(st->keysArray);
        st->keysArray = emalloc(st->n*sizeof(void*));

        minNode = min(st);
        maxNode = max(st);
        st->keysIndex = 0;
        prepareKeys(st, st->root, minNode, maxNode);
        st->keysIndex = 0;

        free(minNode);
        free(maxNode);
    } else{
        st->keysIndex++;
    }

    if(st->keysIndex < st->n){
        return st->keysArray[st->keysIndex];
    } else{
        return NULL;
    }
    

}

void
prepareKeys(RedBlackST st, struct node *x, void *lo, void *hi){
    int cmplo;
    int cmphi;
    void *keyClone;

    if (x == NULL) return; 
    cmplo = st->compar(lo, x->key); 
    cmphi = st->compar(hi, x->key);
    if (cmplo < 0) prepareKeys(st, x->left, lo, hi); 

    if (cmplo <= 0 && cmphi >= 0){
        keyClone = emalloc(x->nKey);
        memcpy(keyClone, x->key, x->nKey);

        st->keysArray[st->keysIndex] = keyClone;
        st->keysIndex++;
    } 

    if (cmphi > 0) prepareKeys(st, x->right, lo, hi); 
}


/*------------------------------------------------------------*/
/* 
 * Funções administrativas
 */

/***************************************************************************
 *  Utility functions.
 ***************************************************************************/

/*
 * HEIGHT(ST)
 * 
 * RECEBE uma RedBlackST e RETORNA a sua altura. 
 * Uma BST com apenas um nó tem altura zero.
 * 
 */
int
height(RedBlackST st)
{
    return heightRecursive(st->root);
}

int
heightRecursive(struct node *x) {
    if (x == NULL) return -1;
    return 1 + mathMax(heightRecursive(x->left), heightRecursive(x->right));
}


/***************************************************************************
 *  Check integrity of red-black tree data structure.
 ***************************************************************************/

/*
 * CHECK(ST)
 *
 * RECEBE uma RedBlackST ST e RETORNA TRUE se não encontrar algum
 * problema de ordem ou estrutural. Em caso contrário RETORNA 
 * FALSE.
 * 
 */
Bool
check(RedBlackST st)
{
    if (!isBST(st))            ERROR("check(): not in symmetric order");
    if (!isSizeConsistent(st)) ERROR("check(): subtree counts not consistent");
    if (!isRankConsistent(st)) ERROR("check(): ranks not consistent");
    if (!is23(st))             ERROR("check(): not a 2-3 tree");
    if (!isBalanced(st))       ERROR("check(): not balanced");
    return isBST(st) && isSizeConsistent(st) && isRankConsistent(st) && is23(st) && isBalanced(st);
}


/* 
 * ISBST(ST)
 * 
 * RECEBE uma RedBlackST ST.
 * RETORNA TRUE se a árvore é uma BST.
 * 
 */
static Bool
isBST(RedBlackST st)
{
    return isBSTRecursive(st, st->root, NULL, NULL);
}

Bool
isBSTRecursive(RedBlackST st, struct node *x, void* min, void* max) {
    if (x == NULL) return TRUE;
    if (min != NULL && st->compar(x->key, min) <= 0) return FALSE;
    if (max != NULL && st->compar(x->key, max) >= 0) return FALSE;
    return isBSTRecursive(st, x->left, min, x->key) && isBSTRecursive(st, x->right, x->key, max);
} 

/* 
 *  ISSIZECONSISTENT(ST) 
 *
 *  RECEBE uma RedBlackST ST e RETORNA TRUE se para cada nó h
 *  vale que size(h) = 1 + size(h->left) + size(h->right) e 
 *  FALSE em caso contrário.
 */
static Bool
isSizeConsistent(RedBlackST st)
{
    return isSizeConsistentRecursive(st->root);
}

Bool
isSizeConsistentRecursive(struct node *x) {
    if (x == NULL) return TRUE;
    if (x->size != sizeProtected(x->left) + sizeProtected(x->right) + 1) return FALSE;
    return isSizeConsistentRecursive(x->left) && isSizeConsistentRecursive(x->right);
} 


/* 
 *  ISRANKCONSISTENT(ST)
 *
 *  RECEBE uma RedBlackST ST e RETORNA TRUE se seus rank() e
 *  select() são consistentes.
 */  
/* check that ranks are consistent */
static Bool
isRankConsistent(RedBlackST st)
{
    int i;
    /*void *key;*/
    for (i = 0; i < sizeProtected(st->root); i++)
        if (i != rank(st, select(st, i))) return FALSE;
    
    /*
    for (key = keys(st, TRUE); key; key = keys(st,FALSE)) {
        if (st->compar(key, (select(st, rank(st, key)))) != 0){
            return FALSE;  
        } 

        
        free(key);

    }
    */
    return TRUE;
}

/* 
 *  IS23(ST)
 *
 *  RECEBE uma RedBlackST ST e RETORNA FALSE se há algum link RED
 *  para a direta ou se ha dois links para esquerda seguidos RED 
 *  Em caso contrário RETORNA TRUE (= a ST representa uma árvore 2-3). 
 */
static Bool
is23(RedBlackST st)
{
    return is23Recursive(st, st->root);
}

Bool
is23Recursive(RedBlackST st, struct node *x) {
    if (x == NULL) return TRUE;
    if (isRed(x->right)) return FALSE;
    if (x != st->root && isRed(x) && isRed(x->left))
        return FALSE;
    return is23Recursive(st, x->left) && is23Recursive(st, x->right);
} 


/* 
 *  ISBALANCED(ST) 
 * 
 *  RECEBE uma RedBlackST ST e RETORNA TRUE se st satisfaz
 *  balanceamento negro perfeiro.
 */ 
static Bool
isBalanced(RedBlackST st)
{
    int black = 0;
    struct node *x = st->root;
    while (x != NULL) {
        if (!isRed(x)) black++;
        x = x->left;
    }
    return isBalancedRecursive(st->root, black);
}

Bool
isBalancedRecursive(struct node *x, int black) {
    if (x == NULL) return black == 0;
    if (!isRed(x)) black--;
    return isBalancedRecursive(x->left, black) && isBalancedRecursive(x->right, black);
} 



/*
Assuming that h is red and both h.left and h.left.left
are black, make h.left or one of its children red.
*/
struct node *
moveRedLeft(struct node *h) {
    /*
    assert (h != null);
    assert isRed(h) && !isRed(h.left) && !isRed(h.left.left);
    */

    flipColors(h);
    if (isRed(h->right->left)) { 
        h->right = rotateRight(h->right);
        h = rotateLeft(h);
        flipColors(h);
    }
    return h;
}

/*
Assuming that h is red and both h.right and h.right.left
are black, make h.right or one of its children red.
*/
struct node *
moveRedRight(struct node *h) {
    /* assert (h != null);
     assert isRed(h) && !isRed(h.right) && !isRed(h.right.left);
    */
    flipColors(h);
    if (isRed(h->left->left)) { 
        h = rotateRight(h);
        flipColors(h);
    }
    return h;
}

/* restore red-black tree invariant */
struct node *
balance(struct node *h) {
    /* assert (h != null);*/

    if (isRed(h->right)){
        h = rotateLeft(h);
    }
    if (isRed(h->left) && isRed(h->left->left)){
        h = rotateRight(h);
    }
    if (isRed(h->left) && isRed(h->right)){
        flipColors(h);   
    }

    
    h->size = sizeProtected(h->left) + sizeProtected(h->right) + 1;
    
    return h;
}