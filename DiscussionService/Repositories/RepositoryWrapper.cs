using DiscussionService.Contracts;
using DiscussionService.Data;

namespace DiscussionService.Repositories
{
    public class RepositoryWrapper : IRepositoryWrapper
    {
        private RepositoryContext _repositoryContext;
        private IQuestionRepository _questionRepository;
        private IAnswerRepository _answerRepository;
        private ISubjectRepository _subjectRepository;
        private IStudentRepository _studentRepository;
        private ILecturerRepository _lecturerRepository;

        public RepositoryWrapper(RepositoryContext repositoryContext)
        {
            _repositoryContext = repositoryContext;
        }

        public IQuestionRepository QuestionRepository
        {
            get
            {
                if (_questionRepository == null)
                {
                    _questionRepository = new QuestionRepository(_repositoryContext);
                }

                return _questionRepository;
            }
        }

        public IAnswerRepository AnswerRepository
        {
            get
            {
                if (_answerRepository == null)
                {
                    _answerRepository = new AnswerRepository(_repositoryContext);
                }

                return _answerRepository;
            }
        }

        public IStudentRepository StudentRepository
        {
            get
            {
                if (_studentRepository == null)
                {
                    _studentRepository = new StudentRepository(_repositoryContext);
                }

                return _studentRepository;
            }
        }

        public ILecturerRepository LecturerRepository
        {
            get
            {
                if (_lecturerRepository == null)
                {
                    _lecturerRepository = new LecturerRepository(_repositoryContext);
                }

                return _lecturerRepository;
            }
        }

        public ISubjectRepository SubjectRepository
        {
            get
            {
                if (_subjectRepository == null)
                {
                    _subjectRepository = new SubjectRepository(_repositoryContext);
                }

                return _subjectRepository;
            }
        }

        public async Task SaveAsync()
        {
            await _repositoryContext.SaveChangesAsync();
        }
    }
}