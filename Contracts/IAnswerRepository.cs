using DiscussionService.Dtos;
using DiscussionService.Helpers;
using DiscussionService.Models;

namespace DiscussionService.Contracts
{
    public interface IAnswerRepository : IRepositoryBase<Answer>
    {
        Task<PagedList<Answer>> GetAllAnswersAsync(AnswersQueryStringParameters answersQueryStringParameters);
        Task<Answer> GetAnswerByIdAsync(Guid answerId);
        Task<IEnumerable<Answer>> GetAnswersByStudentId(Guid studentId);
        Task<IEnumerable<Answer>> GetAnswersRemovedByLecturer(String lecturerEmail);
        void CreateAnswer(Answer answer);
        void UpdateAnswer(Answer answer);
        void DeleteAnswer(Answer answer);
    }
}