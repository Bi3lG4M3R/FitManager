# Relatório — FitManager (Stage 2)

## Introdução

O sistema FitManager foi evoluído nesta segunda etapa para incorporar os seguintes conceitos: Classes abstratas, herança, polimorfismo e interfaces Java. Essas abordagens organizam nossa hierarquia de classes, generalizam comportamentos comuns e especializam regras específicas das entidades.

Durante esta etapa, substituímos as classes concretas estáticas `Plan` e `Payment` por superclasses abstratas, criando subclasses para cada tipo correspondente. Também transformamos a `UserInterface` num contrato formal (Interface Java) para possibilitar a interação via Console ou Interface Gráfica, mantendo todas as regras de negócio consistentes.

## Integrantes e contribuições

- **Matheus Henrique dos Santos Gomes:** Refatoração e separação da classe de Domínio (`Plan`), implementando a superclasse abstrata e as respectivas regras matemática da subclasse. Refatoração do Service e Integração (`FitManager` e `EnrollmentService`) para suportar instâncias polimórficas de Pagamento e Plano. Correção de alguns bugs vindos da etapa anterior (`Stage-1`).
- **Gabriel Richard Zambianchi de Oliveira:** Adaptação da camada de Interface de Usuário. Implementação da interface `UserInterface` e sua realização nas classes `TerminalUI` e `JOptionPaneUI`, bem como o roteamento das classes concretas nos Menus.
- **Matheus Mandarini:** Correção e refinamento da etapa anterior (`Stage-1`). Refatoração do Service e Integração (`FitManager` e `EnrollmentService`) para suportar instâncias polimórficas de Pagamento, delegando a responsabilidade de criação para blocos de decisão coesos.

## Decisões de Projeto e Análise de Arquitetura

### 1. O que pertence às superclasses abstratas (`Plan` e `Payment`)?

Após análise, concentramos na superclasse `Plan` os atributos que definem a universalidade de um plano: `name`, `description`, `minDurationMonths` e `pricePerMonth`. Tudo que altera o cálculo matemático do sistema foi delegado aos métodos abstratos/sobrescritos `calculateTotalPrice` e `getCancellationFee`.

Em `Payment`, identificamos que atributos como `date`, `amount` e `description` são genuinamente universais. O atributo `amountReceived` ou as `installments` são exclusivos de modalidades de pagamento específicas, portando foram posicionados em subclasses como `CashPayment` e `CreditCardPayment`. Criamos os métodos abstratos `getProcessingFee()` e `getPaymentSummary()` para lidar com taxas financeiras e recibos formatados.

### 2. Herança versus Composição

Verificamos a semântica do sistema: um `AnnualPlan` **é um tipo de** `Plan`, assim como um `CreditCardPayment` **é um tipo de** `Payment`. Essa constatação justifica o uso de herança. Composição seria usada se um Plano "tivesse" uma estratégia de pagamento avulsa ou se fosse apenas um anexo ao contrato. A herança aplicada aqui centraliza o código base e garante a aderência ao Liskov Substitution Principle.

### 3. Interface vs. Classe Abstrata para `UserInterface`

Optamos por criar uma **Interface Java** pura (`interface UserInterface`) porque `TerminalUI` e `JOptionPaneUI` não compartilham atributos de estado, variáveis, construtores em comum ou lógica interna. A classe via Terminal usa `Scanner` e `System.out.println`, enquanto a interface gráfica utiliza instâncias do `JOptionPane`. A única ligação entre elas é o contrato estabelecido das assinaturas dos métodos, algo que a Interface Java atende perfeitamente, garantindo baixo acoplamento.

### 4. Onde o Polimorfismo simplifica o código existente?

No stage-1, se precisássemos calcular as diferentes taxas de cancelamento ou totais pagos, encheríamos o `EnrollmentService` e a classe `Enrollment` com lógicas condicionais atreladas a `Enums` (`switch(plan.getType())`).
Agora, na chamada polimórfica `enrollment.getPlan().getCancellationFee(enrollment)` ou `payment.getProcessingFee()`, o código externo desconhece qual subclasse está respondendo. As condificionais foram totalmente eliminadas dessas operações de negócio vitais.

### 5. Instanciação da subclasse correta no serviço

No sistema, o `PlanService` agora usa um bloco `switch` no momento em que os dados são enviados do Menu (baseado no `PlanType` do Enum) para construir a classe instanciada correta (`MonthlyPlan`, `AnnualPlan`, etc.). Da mesma forma, no menu de Matrícula e Pagamento, sobrecargas de métodos (`enrollStudent`) no `FitManager` instanciam a subclasse de pagamento correspondente (`PixPayment`, `CashPayment`...) antes de mandá-las para a base de dados via Polimorfismo.

### 6. O Enum `PlanType` e `PaymentType` ainda tem papel no sistema?

Sim, optamos por mantê-los. Eles provaram ser fundamentais para exibir descrições limpas nos Menus de Usuário (camada de UI) e atuar como "Roteadores" seguros na hora que o usuário deve escolher qual classe concreta o construtor do sistema precisa usar (sem que o usuário precise entender o código em si).

### 7. Taxa de cancelamento: Semântica no Domínio

A taxa de cancelamento foi definida no domínio como uma multa monetária explícita que o cliente deve arcar para encerrar o vínculo. No caso exclusivo do `AnnualPlan`, se ele usar menos da metade do tempo acordado, ele pagará uma fatura extra que representa 20% do valor de contrato original. No sistema, essa regra é tratada por meio do `EnrollmentMenu`, que obriga o registro de um novo pagamento antes de prosseguir com a troca de status para `CANCELLED`. Planos avulsos como Mensal retornam `0.0` em sua sobrescrita natural.

## Regras de negócio evoluídas nesta etapa

- **Desconto por longo prazo de Planos:** Subclasses como `QuarterlyPlan` e `AnnualPlan` encapsularam fórmulas de redução em seu `calculateTotalPrice`.
- **Multa de quebra de contrato (`getCancellationFee`):** Apenas presente de forma afirmativa no Plano Anual caso os meses utilizados não atinjam 50% do contrato.
- **Taxa de Processamento de Cartão (`getProcessingFee`):** O Cartão de crédito assume 2,5% de perda que a academia absorve nas suas finanças. O código da subclasse `CreditCardPayment` garante essa abstração.

## Dificuldades e aprendizados

A maior dificuldade da refatoração da Segunda Etapa (`Stage-2`) foi limpar a mente para **remover lógicas específicas que estavam na superclasse**. Especialmente no início do refatoramento da camada `Payment`, tínhamos um reflexo de colocar todos os dados na classe mãe para "reaproveitar" o código, criando anomalias como um `PixPayment` possuindo "Parcelas", mas logo esse reflexo foi desaparecendo.

O aprendizado chave foi que classes abstratas bem desenhadas não servem para concentrar "todos os campos", e sim para isolar o que é genuinamente universal, encapsulando no Polimorfismo as especificidades, deixando o restante da aplicação muito mais fácil de manter.
