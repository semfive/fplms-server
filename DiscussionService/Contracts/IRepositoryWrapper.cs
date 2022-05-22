namespace DiscussionService.Contracts
{
    public interface IRepositoryWrapper
    {
        IQuestionRepository QuestionRepository { get; }
        IAnswerRepository AnswerRepository { get; }
        void Save();
    }
}