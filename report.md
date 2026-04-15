# Relatório — FitManager (Stage 1)

## Introdução

O sistema FitManager tem como objetivo simular o gerenciamento de uma academia, permitindo o controle de alunos, planos, matrículas e pagamentos. Nesta primeira etapa (stage-1), foi implementada a base funcional do sistema, com foco na modelagem do domínio, organização em camadas e implementação das operações essenciais.

O sistema foi estruturado seguindo os princípios de Programação Orientada a Objetos, com separação entre interface, aplicação e domínio. Foram implementadas funcionalidades como cadastro, consulta, listagem e controle de regras de negócio, preparando o projeto para evoluções futuras.

## Integrantes e contribuições

- Matheus Henrique dos Santos Gomes: desenvolvimento das classes mais desacopladas do núcleo do sistema, focando na modelagem do domínio e estrutura base.
- Gabriel Richard Zambianchi de Oliveira: implementação dos menus e da interação com o usuário (camada de interface).
- Matheus Mandarini de Menezes: responsável pela integração entre as classes, com foco nas partes centrais do sistema, especialmente relacionadas às matrículas (Enrollment).

## Decisões de projeto

### Armazenamento do CPF

- Decisão: CPF armazenado como String.
- Alternativas: armazenar como int ou long.
- Motivo: CPF pode conter zeros à esquerda e não é utilizado para cálculos.
- Impacto: evita perda de informação e facilita validação.

### totalPrice como atributo da matrícula

- Decisão: armazenar o valor total no momento da criação da matrícula.
- Alternativas: calcular dinamicamente com base no plano.
- Motivo: garantir que mudanças futuras no plano não afetem contratos antigos.
- Impacto: mantém consistência histórica dos dados.

### Estratégia de remoção de alunos

- Decisão: impedir remoção de alunos com matrícula ativa.
- Alternativas: remover diretamente ou apenas desativar o aluno.
- Motivo: evitar inconsistência no sistema.
- Impacto: garante integridade dos dados.

### Regra de pagamento inicial

- Decisão: exigir pagamento inicial para efetivar matrícula.
- Alternativas: permitir matrícula sem pagamento inicial.
- Motivo: refletir um cenário mais realista.
- Impacto: garante vínculo financeiro válido para cada matrícula.

### Lógica de cancelamento de matrícula

- Decisão: cancelar matrícula alterando o status para CANCELLED.
- Alternativas: remover matrícula da lista.
- Motivo: preservar histórico de operações.
- Impacto: permite rastreamento de dados antigos.

### Organização em camadas

- Decisão: separar o sistema em UI, Application e Domain.
- Alternativas: concentrar toda a lógica em uma única camada.
- Motivo: facilitar manutenção e evolução do sistema.
- Impacto: organização e escalabilidade.

## Regras de negócio implementadas

- CPF deve ser único
  → Implementado em StudentService.cpfExists()

- Nome do plano deve ser único
  → Implementado em PlanService.nameExists()

- Não permitir mais de uma matrícula ativa por aluno
  → Implementado em EnrollmentService.hasActiveEnrollment()

- Duração da matrícula deve respeitar o mínimo do plano
  → Implementado em EnrollmentService.enroll()

- Pagamento deve ser positivo
  → Implementado em EnrollmentService.registerPayment()

- Pagamentos só podem ser feitos em matrículas ativas
  → Implementado em EnrollmentService.registerPayment()

- Cálculo de total pago e saldo
  → Implementado em Enrollment.calculateTotalPaid() e calculateBalance()

- Impedir remoção de aluno com matrícula ativa
  → Implementado em FitManager.removeStudent()

## Dificuldades e aprendizados

Durante o desenvolvimento, a principal dificuldade foi organizar corretamente as responsabilidades entre as camadas, especialmente separar o que deveria ficar nos serviços e no FitManager. Também houve desafios na integração entre as classes, principalmente envolvendo matrículas e pagamentos. Outra dificuldade foi estruturar a interação via menus sem misturar lógica de negócio com interface.

Como aprendizado, nós compreendemos melhor a importância da separação de responsabilidades e da organização do código. Caso o projeto fosse reiniciado, seria feita uma definição mais detalhada das responsabilidades antes da implementação, a fim de evitar retrabalho.
