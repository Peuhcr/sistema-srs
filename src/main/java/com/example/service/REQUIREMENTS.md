## SERVICE LAYER

-- Camada de serviços. Contém apenas regras do sistema (autenticação, permissão, conflito, disponibilidade, cadastramento).
-- Services recebem o usuário logado (ator) como parâmetro explícito. Não conhecem sessão nem HTTP.
-- Services chamam DAOs. Não executam SQL nem abrem conexões.
-- Erros de regra são lançados como exceções de domínio (pacote exception).

-- Classes do MVP:

    -- AuthService
    -- UsuarioService
    -- ReservaService
    -- PasswordHasher (auxiliar)

-- Fora do MVP por ora: SalaService, OrganizacaoService (CRUD fino, com checagem de admin, a fazer depois).

-- AuthService:

    -- public Usuario login (String email, String senha)
        -- busca a credencial por e-mail (findByEmailForAuthentication)
        -- compara a senha com o hash (PasswordHasher.verificar)
        -- se e-mail não existir ou senha estiver errada, lança CredencialInvalidaException com mensagem genérica
        -- devolve o Usuario completo (findById)
    -- O logout não é regra de service: o controller invalida a sessão.

-- UsuarioService:

    -- public Usuario cadastrar (Usuario ator, Usuario novo, String senhaPlana)
        -- somente ADMIN pode cadastrar (senão AcessoNegadoException). Evita cadastro público como ADMIN.
        -- senha com no mínimo 8 caracteres
        -- MEMBRO precisa de organização; ADMIN não pode ter organização (espelha a CHECK do banco)
        -- e-mail não pode estar cadastrado (senão EmailJaCadastradoException)
        -- gera o hash (PasswordHasher) e grava com insert

-- ReservaService:

    -- public Reserva criar (Usuario ator, Reserva reserva)
        -- ator precisa ter organização
        -- sala deve existir (SalaDao.findByID)
        -- dataInicio < dataFim
        -- dataInicio não pode estar no passado
        -- 1 <= numParticipantes <= capacidade da sala
        -- não pode haver conflito (existeConflito); a constraint do banco continua como garantia
        -- o service define responsável = ator, organização = organização do ator, status = PENDENTE, aprovador = null
    -- public Reserva aprovar (Usuario ator, Reserva reserva)  -> somente ADMIN
    -- public Reserva recusar (Usuario ator, Reserva reserva)  -> somente ADMIN
    -- public List<Reserva> listarPendentes (Usuario ator)     -> somente ADMIN
    -- public List<Reserva> minhasReservas (Usuario ator)      -> qualquer usuário (findByUser)

-- Permissões (resumo):

    -- ADMIN: cadastra usuários, aprova, recusa, lista pendentes.
    -- MEMBRO: cria reservas, acompanha as próprias reservas.
    -- Sala e organização: somente ADMIN altera (a implementar com SalaService e OrganizacaoService).

-- Hash de senha:

    -- PBKDF2WithHmacSHA256, 310 000 iterações, salt aleatório de 16 bytes por usuário.
    -- Formato armazenado: pbkdf2_sha256$<iterações>$<salt Base64>$<hash Base64>.
    -- Comparação com MessageDigest.isEqual (tempo constante).

-- Exceções (pacote exception):

    -- AcessoNegadoException, CredencialInvalidaException (novas)
    -- EmailJaCadastradoException, ReservaConflitoException (já existiam)
    -- Erros de validação simples usam IllegalArgumentException.

-- Pendências:

    -- Filtro /admin/* (camada de controller/web, a fazer).
    -- Quem pode se cadastrar além de admin (assumido: só admin, confirmar).
    -- Admin criar reservas (assumido: não, pois admin não tem organização, confirmar).
