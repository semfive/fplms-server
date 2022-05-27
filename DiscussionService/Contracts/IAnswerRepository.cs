using DiscussionService.Models;

namespace DiscussionService.Contracts
{
    public interface IAnswerRepository : IRepositoryBase<Answer>
    {
        Task<IEnumerable<Answer>> GetAllAnswersAsync();
        Task<Answer> GetAnswerByIdAsync(Guid answerId);
        Task<IEnumerable<Answer>> GetAnswersByStudentId(Guid studentId);
        Task<IEnumerable<Answer>> GetAnswersRemovedByLecturerId(Guid lecturerId);
        void CreateAnswer(Answer answer);
        void UpdateAnswer(Answer answer);
        void DeleteAnswer(Answer answer);
    }
}