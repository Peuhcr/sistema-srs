## DAO LAYER



## ReservaDao

-- Essa classe deverá ser responsável pela execução de queries e updates referentes apenas às tabelas reservas e as tabelas associadas, quando houver JOIN, em SQL no banco de dados da aplicação.

-- Métodos públicos (MVP):

    -- public Reserva aprovarReserva (Reserva reserva, Usuario aprovador)
    -- public Reserva recusarReserva (Reserva reserva, Usuario aprovador)
    -- public List<Reserva> findPendentes ()
    -- public boolean existeConflito (long salaId, LocalDateTime inicio, LocalDateTime fim)
    -- public Optional <Reserva> findById (Reserva reserva)
    -- public List<Reserva> findByUser (Usuario usuario)
    -- CRUD
    

-- Regras:

    -- aprovarReserva e recusarReserva delegam para um método privado assistente (ex.: atualizarStatus) que contém o UPDATE.
    -- aprovador é passado como parâmetro apenas na aprovação.
    -- O UPDATE só altera reservas com status = 'pendente' (WHERE id = ? AND status = 'pendente'). Se nenhuma linha for afetada, lançar exceção.
    -- Os métodos devolvem a reserva relida do banco, com os JOINs de sala, responsável, aprovador e organização.
    -- Conversão entre enum Java (PENDENTE, APROVADA, RECUSADA) e banco (pendente, aprovada, recusada) fica no DAO.





    
