# 💪 FitManager

Sistema de gerenciamento para academias desenvolvido como parte da disciplina da UFMS.

## 📌 Sobre o projeto

O **FitManager** é um sistema que simula o funcionamento básico de uma academia, permitindo o gerenciamento de alunos, planos, matrículas e pagamentos.

Esta primeira etapa tem como foco principal a **estruturação do sistema utilizando Programação Orientada a Objetos (POO)** e a organização em camadas, preparando o projeto para evoluções futuras.

---

Partes implementadas:

### 📦 Domain

Responsável por representar as entidades do sistema:

- `Student`
- `Plan`
- `Enrollment`
- `Payment`
- Enums:
  - `PlanType`
  - `PaymentType`
  - `EnrollmentStatus`

### ⚙️ Application

Responsável pela lógica de aplicação e orquestração:

- `EnrollmentService`
- `OperationResult`

---

## 🧠 Decisões de projeto

### 🔹 Uso de `OperationResult`

Todas as operações retornam um objeto padronizado contendo:

- `success`: indica se a operação foi bem-sucedida
- `message`: mensagem descritiva
- `data`: informação adicional (atualmente usando `LocalDate`)

Essa abordagem evita uso de exceções para fluxo normal e padroniza o tratamento de erros.

---

### 🔹 Preparação para evolução

O projeto já foi estruturado pensando nas próximas etapas:

- Uso de enums (`PlanType`, `PaymentType`) → futura evolução para herança
- `OperationResult` → preparado para uso com generics
- Separação em pacotes (`domain`, `application`) → facilita manutenção

---

## 🛠 Tecnologias utilizadas

- Java 17+
- API `java.time` (LocalDate)
- Estruturas de dados:
  - `ArrayList`

---
