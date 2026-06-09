# FitManager
## Sistema de Gestão de Academia

# Relatório de Projeto — Etapa 3

## 1. Introdução da Etapa 3

A terceira etapa consolidou o FitManager com recursos que elevam sua qualidade ao nível exigido por aplicações reais.

Principais frentes:
- Generics (`OperationResult<T>` e `Repository<T>`)
- Hierarquia de exceções
- Persistência em arquivos CSV
- Relatório financeiro mensal

## 2. Integrantes e Contribuições

| Integrante | Contribuições |
|------------|---------------|
| Matheus Henrique | Repository<T>, PlanService, persistência CSV, relatório financeiro |
| Gabriel Richard | Refatoração dos menus e eliminação de casts explícitos |
| Matheus Mandarini | Hierarquia de exceções, StudentService e EnrollmentService com persistência CSV |

## 3. Diagrama de Classes — Etapa 3

O diagrama reflete a arquitetura final do sistema, incluindo:
- `Repository<T>`
- `OperationResult<T>`
- Hierarquia de exceções
- `FinancialReport`
- Métodos polimórficos `getTypeName()`

## 4. Decisões de Projeto

### 4.1 OperationResult<T>
- Eliminação de casts explícitos.
- Uso de `OperationResult<Void>` para operações sem retorno.

### 4.2 Repository<T>
- Centralização de comportamento comum.
- Persistência obrigatória através de métodos abstratos.

### 4.3 Hierarquia de Exceções
- `PersistenceException` (checked)
- `BusinessException` (unchecked)
- `ValidationException` (unchecked)

### 4.4 Persistência CSV
- Arquivos texto legíveis.
- Preservação de polimorfismo via discriminadores de tipo.
- Ordem de carregamento: alunos → planos → matrículas.

### 4.5 Salvamento Incremental
- Persistência após operações relevantes.
- Minimização de perda de dados.

### 4.6 Relatório Financeiro
- Agrupamento polimórfico por `getTypeName()`.
- Retorno válido mesmo sem dados no período.

## 5. Generics e Segurança de Tipos

### OperationResult<T>
- `Object` substituído por `T`.
- Tipagem verificada em compilação.

### Repository<T>
- Eliminação de código duplicado.
- Coleções totalmente genéricas.

## 6. Evidências de Funcionamento

### Persistência
- `plans.csv`
- `students.csv`
- `enrollments.csv`

### Polimorfismo
- Reconstituição correta de planos e pagamentos após recarga.

### Relatório Financeiro
- Receita total.
- Taxas de processamento.
- Receita líquida.
- Agrupamentos por plano e forma de pagamento.

## 7. Política de Exceções

### OperationResult
Utilizado para falhas de negócio dentro do fluxo normal.

### Exceções
Utilizadas para situações que interrompem o fluxo da aplicação.

## 8. Hierarquia de Exceções

```text
Exception
└── FitManagerException
    ├── PersistenceException
    │   ├── CorruptedFileException
    │   └── WriteFailureException
    ├── BusinessException
    └── ValidationException
```

## 9. Dificuldades e Aprendizados

- Refatoração em cascata de `OperationResult<T>`.
- Preservação do polimorfismo na persistência.
- Controle do `nextCode`.
- Correções de bugs residuais.

### Melhorias Futuras
- Definir convenções de retorno desde a Etapa 1.
- Planejar persistência antecipadamente.
- Formalizar o formato CSV.
- Utilizar branches de feature desde o início.
