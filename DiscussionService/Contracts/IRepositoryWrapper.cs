namespace DiscussionService.Contracts
{
    public interface IRepositoryWrapper
    {
        IQuestionRepository QuestionRepository { get; }
        IAnswerRepository AnswerRepository { get; }
        IStudentRepository StudentRepository { get; }
        ISubjectRepository SubjectRepository { get; }
        Task SaveAsync();
    }
}