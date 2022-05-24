using DiscussionService.Models;

namespace DiscussionService.Contracts
{
    public interface IAnswerRepository : IRepositoryBase<Answer>
    {
        Task<IEnumerable<Answer>> GetAllAnswersAsync();
        Task<Answer> GetAnswerByIdAsync(Guid answerId);
        void CreateAnswer(Answer answer);
        void UpdateAnswer(Answer answer);
        void DeleteAnswer(Answer answer);
    }
}