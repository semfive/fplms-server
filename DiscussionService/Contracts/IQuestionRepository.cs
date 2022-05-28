using DiscussionService.Dtos;
using DiscussionService.Helpers;
using DiscussionService.Models;

namespace DiscussionService.Contracts
{
    public interface IQuestionRepository : IRepositoryBase<Question>
    {
        Task<PagedList<Question>> GetAllQuestionsAsync(QueryStringParameters queryStringParameters);
        Task<Question> GetQuestionByIdAsync(Guid questionId);
        Task<IEnumerable<Question>> GetQuestionsByStudentId(Guid studentId);
        Task<IEnumerable<Question>> GetQuestionsRemovedByLecturerId(Guid lecturerId);
        void CreateQuestion(Question question);
        void UpdateQuestion(Question question);
        void DeleteQuestion(Question question);
    }
}